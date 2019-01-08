/* Copyright (C) 2013-2016, The Regents of The University of Michigan.
All rights reserved.
This software was developed in the APRIL Robotics Lab under the
direction of Edwin Olson, ebolson@umich.edu. This software may be
available under alternative licensing terms; contact the address above.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the Regents of The University of Michigan.
*/

package april.tag;

import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.imageio.*;

public class TagFamilyGenerator
{
    private final ImageLayout layout;
    int nbits;
    int minhamming;

    ArrayList<Long> codelist;
    long starttime;

    long rotcodes[] = new long[16384];
    int nrotcodes = 0;

    static final long PRIME = 982451653;

    public TagFamilyGenerator(ImageLayout layout, int minhamming)
    {
        this.layout = layout;
        this.nbits = layout.getNumBits();
        this.minhamming = minhamming;
    }

    static final void printBoolean(PrintStream outs, long v, int nbits)
    {
        for (int b = nbits-1; b >= 0; b--)
            outs.printf("%d", (v & (1L<<b)) > 0 ? 1 : 0);
    }

    static final void printCodes(long codes[], int nbits)
    {
        for (int i = 0; i < codes.length; i++) {
            long w = codes[i];
            System.out.printf("%5d ", i);
            printBoolean(System.out, w, nbits);
            System.out.printf("    %0"+((int) Math.ceil(nbits/4))+"x\n", w);
        }
    }

    public static void main(String args[])
    {
        if (args.length < 2) {
            System.out.println("usage: <layout> <minhammingdistance>");
            System.out.println("There are several options for layout.");
            System.out.println("\t1. To get a new-style april tag with one pixel of data on " +
                    "the outside use \"standard_x\" where x is the width of the desired tag.");
            System.out.println("\t2. To get an old-style april tag use \"classic_x\" where " +
                    "x is the width of the desired tag.");
            System.out.println("\t3. To get a circular april tag use \"circle_x\" where " +
                    "x is the width of the desired tag.");
            System.out.println("\t4. To get an april tag with a custom layout, use \"custom_\" " +
                    "followed by a string to " +
                    "indicate which type each pixel should be. Use d for data pixels, w for white " +
                    "border, b for black border, x for pixels which should not be part of the tag, " +
                    "and r for pixels which should be part of the recursive version of the tag. " +
                    "For example, custom_wwwwwwwwwbbbbbbwwbddddbwwbddddbwwbddddbwwbddddbwwbbbbbbwwwwwwwww " +
                    "would give an old-style tag with a 4x4 data block in the center.");
            return;
        }

        ImageLayout layout = LayoutUtil.getLayout(args[0]);
        int minhamming = Integer.parseInt(args[1]);

        TagFamilyGenerator tfg = new TagFamilyGenerator(layout, minhamming);
        tfg.compute();
        tfg.report();
    }

    boolean isCodePartiallyOkay(long v, long nRotCodesPartial)
    {
        // tag must be reasonably complex
        if (!isComplexEnough(v)) {
            return false;
        }

        // The tag must be different from itself when rotated.
        long rv1 = TagFamily.rotate90(v, nbits);
        long rv2 = TagFamily.rotate90(rv1, nbits);
        long rv3 = TagFamily.rotate90(rv2, nbits);

        if (!hammingDistanceAtLeast(v, rv1, minhamming) ||
            !hammingDistanceAtLeast(v, rv2, minhamming) ||
            !hammingDistanceAtLeast(v, rv3, minhamming) ||
            !hammingDistanceAtLeast(rv1, rv2, minhamming) ||
            !hammingDistanceAtLeast(rv1, rv3, minhamming) ||
            !hammingDistanceAtLeast(rv2, rv3, minhamming)) {

            return false;
        }

        // tag must be different from other tags.
        for (int widx = 0; widx < nRotCodesPartial; widx++) {
            long w = rotcodes[widx];

            if (!hammingDistanceAtLeast(v, w, minhamming)) {
                return false;
            }
        }

        return true;
    }

    boolean isComplexEnough(long v) {
        int energy = 0;
        int[][] tag = layout.renderToArray(v);
        for (int y = 0; y < tag.length; y++) {
            for (int x = 0; x < tag.length - 1; x++) {
                if ((tag[y][x] == 0 && tag[y][x + 1] == 1) || (tag[y][x] == 1 && tag[y][x + 1] == 0)) {
                    energy++;
                }
            }
        }

        for (int x = 0; x < tag.length; x++) {
            for (int y = 0; y < tag.length - 1; y++) {
                if ((tag[y][x] == 0 && tag[y+1][x] == 1) || (tag[y][x] == 1 && tag[y+1][x] == 0)) {
                    energy++;
                }
            }
        }

        int area = 0;
        for (int y = 0; y < tag.length; y++) {
            for (int x = 0; x < tag.length; x++) {
                if (tag[y][x] == 0 || tag[y][x] == 1) {
                    area++;
                }
            }
        }

        int maxEnergy = 2*area;
        return energy >= 0.3333*maxEnergy;
    }

    void union(int[][] components, int x0, int y0, int x1, int y1) {
        int c0 = components[y0][x0];
        int c1 = components[y1][x1];

        for (int y = 0; y < components.length; y++) {
            for (int x = 0; x < components.length; x++) {
                if (components[y][x] == c1) {
                    components[y][x] = c0;
                }
            }
        }
    }

    boolean isCodePartiallyOkay2(long v, int nRotCodesPartial) {
        // tag must be different from other tags.
        for (int widx = nRotCodesPartial; widx < nrotcodes; widx++) {
            long w = rotcodes[widx];

            if (!hammingDistanceAtLeast(v, w, minhamming)) {
                return false;
            }
        }
        return true;
    }

    class PartialApprovalTask implements Runnable {
        private HashMap<Long, PartialApprovalResult> map;
        private int nRotCodesPartial;
        private long V0;
        private long iter0;
        private long iter1;

        PartialApprovalTask(HashMap<Long, PartialApprovalResult> map, int nRotCodesPartial, long V0, long iter0, long iter1) {
            this.map = map;
            this.nRotCodesPartial = nRotCodesPartial;
            this.V0 = V0;
            this.iter0 = iter0;
            this.iter1 = iter1;
        }

        @Override
        public void run() {
            // compute v = V0 + PRIME * iter0,
            // being very careful about overflow.
            // (consider the power-of-two expansion of iter0....)
            long v = V0;

            long acc = PRIME;
            long M = iter0;
            while (M > 0) {
                if ((M & 1) > 0) {
                    v += acc;
                    v &= ((1L<<nbits) - 1);
                }

                acc *= 2;
                acc &= ((1L << nbits) - 1);
                M >>= 1;
            }

            PartialApprovalResult result = new PartialApprovalResult();
            result.nRotCodesPartial = nRotCodesPartial;
            result.iter0 = this.iter0;
            result.iter1 = this.iter1;

            for (long iter = iter0; iter < iter1; iter++) {
                v += PRIME; // big prime.
                v &= ((1L<<nbits) - 1);

                if (isCodePartiallyOkay(v, nRotCodesPartial)) {
                    result.goodCodes.add(v);
                }
            }

            synchronized (map) {
                map.put(result.iter0, result);
            }
        }
    }

    class PartialApprovalResult {
        ArrayList<Long> goodCodes = new ArrayList<Long>();
        int nRotCodesPartial;
        Long iter0;
        Long iter1;
    }

    class ReportingThread extends Thread {
        public void run() {
            long lastreporttime = System.currentTimeMillis();
            long lastNumCodes = 0;

            while (!Thread.interrupted()) {
                // print a partial report.
                if ((System.currentTimeMillis() - lastreporttime > 60 * 60 * 1000) ||
                        (codelist.size() > 1.1*lastNumCodes && System.currentTimeMillis() - lastreporttime > 60*1000)) {
                    report();
                    lastreporttime = System.currentTimeMillis();
                    lastNumCodes = codelist.size();
                }

                try {
                    Thread.sleep(10*1000L);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    class ApprovalThread extends Thread {
        private HashMap<Long, PartialApprovalResult> map;
        private Long iter;

        ApprovalThread(HashMap<Long, PartialApprovalResult> map) {
            this.map = map;
            this.iter = 0L;
        }

        public void run() {
            while (true) {
                if (iter == (1L << nbits)) {
                    return;
                }

                PartialApprovalResult result;
                while(true) {
                    synchronized (map) {
                        if (map.containsKey(iter)) {
                            result = map.remove(iter);
                            this.iter = result.iter1;
                            break;
                        }
                    }

                    try {
                        sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (Long v : result.goodCodes) {
                    if (isCodePartiallyOkay2(v, result.nRotCodesPartial)) {
                        synchronized (codelist) {
                            codelist.add(v);
                            long rv1 = TagFamily.rotate90(v, nbits);
                            long rv2 = TagFamily.rotate90(rv1, nbits);
                            long rv3 = TagFamily.rotate90(rv2, nbits);

                            // grow?
                            if (nrotcodes + 4 >= rotcodes.length) {
                                long newrotcodes[] = new long[rotcodes.length*2];
                                System.arraycopy(rotcodes, 0, newrotcodes, 0, rotcodes.length);
                                rotcodes = newrotcodes;
                            }

                            rotcodes[nrotcodes++] = v;
                            rotcodes[nrotcodes++] = rv1;
                            rotcodes[nrotcodes++] = rv2;
                            rotcodes[nrotcodes++] = rv3;
                        }
                    }
                }
            }
        }
    }

    public TagFamily compute()
    {
        assert(codelist == null);

        codelist = new ArrayList<Long>(); // code lists
        starttime = System.currentTimeMillis();

        // begin our search at a random position to avoid any bias
        // towards small numbers (which tend to have larger regions of
        // solid black).
        long V0 = new Random(nbits*10000 + minhamming*100 + 7).nextLong();

        long lastprogresstime = starttime;
        long lastprogressiters = 0;

        int nthreads = Runtime.getRuntime().availableProcessors();
        System.out.printf("Using %d threads.\n", nthreads);


        ThreadPoolExecutor pool = new ThreadPoolExecutor(nthreads, nthreads, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        HashMap<Long, PartialApprovalResult> map = new HashMap<Long, PartialApprovalResult>();
        int mapMax = 300;

        Thread approvalThread = new ApprovalThread(map);
        approvalThread.start();
        Thread reportingThread = new ReportingThread();
        reportingThread.start();

        long iter = 0;
        long chunksize = 50000;

        while (true) {
            // print a progress report.
            long now = System.currentTimeMillis();
            if (now - lastprogresstime > 5000) {

                double donepercent = (iter*100.0)/(1L<<nbits);
                double dt = (now - lastprogresstime)/1000.0;
                long diters = iter - lastprogressiters;
                double rate = diters / dt; // iterations per second
                double secremaining = ((long) (1L<<nbits) - iter) / rate;
                synchronized (System.out) {
                    System.out.printf("%8.4f%%  codes: %-5d (%.0f iters/sec, %.2f minutes = %.2f hours)           \n",
                            donepercent, codelist.size(), rate, secremaining / (60.0), secremaining / 3600.0);
                }
                lastprogresstime = now;
                lastprogressiters = iter;
            }

            if (pool.getQueue().size() == 0) {
                if (iter < 1L << nbits) {
                    boolean addTask = false;
                    synchronized (map) {
                        addTask = map.size() < mapMax;
                    }

                    if (addTask) {
                        synchronized (codelist) {
                            long iter0 = iter;
                            iter = Math.min(iter + chunksize, 1L << nbits);
                            pool.execute(new PartialApprovalTask(map, codelist.size(), V0, iter0, iter));
                        }
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                }
            }

            if (!approvalThread.isAlive()) {
                System.out.println("Approval thread dead. Done!");
                break;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Interrupted.");
                e.printStackTrace();
            }
        }

        reportingThread.interrupt();
        pool.shutdown();

        long codes[] = new long[codelist.size()];
        for (int i = 0; i < codelist.size(); i++) {
            codes[i] = codelist.get(i);
        }

        return new TagFamily(layout, minhamming, codes);
    }

    void report()
    {
        int nCodes;
        synchronized (codelist) {
            nCodes = codelist.size();
        }
        long codes[] = new long[nCodes];
        for (int i = 0; i < nCodes; i++)
            codes[i] = codelist.get(i);

        int hds[] = new int[nbits+1];
        int hdtotal = 0;

        // compute hamming distance table
        for (int i = 0; i < nCodes; i++) {
            long rv0 = codelist.get(i);
            long rv1 = TagFamily.rotate90(rv0, nbits);
            long rv2 = TagFamily.rotate90(rv1, nbits);
            long rv3 = TagFamily.rotate90(rv2, nbits);

            for (int j = i+1; j < nCodes; j++) {
                int dist = Math.min(Math.min(hammingDistance(rv0, codelist.get(j)),
                                             hammingDistance(rv1, codelist.get(j))),
                                    Math.min(hammingDistance(rv2, codelist.get(j)),
                                             hammingDistance(rv3, codelist.get(j))));


                hds[dist]++;
                if (dist < minhamming) {
                    System.out.printf("ERROR, dist = %3d: %d %d\n", dist, i, j);
                }
                hdtotal++;
            }
        }

        synchronized (System.out) {
            System.out.printf("\n\npackage april.tag;\n\n");
            String cname = String.format("Tag%s%dh%d", layout.getName(), nbits, minhamming);
            System.out.printf("/** Tag family with %d distinct codes.\n", codes.length);
            System.out.printf("    bits: %d,  minimum hamming: %d\n\n", nbits, minhamming);

            // compute some ROC statistics, assuming randomly-visible targets
            // as a function of how many bits we're willing to correct.
            System.out.printf("    Max bits corrected       False positive rate\n");

            for (int cbits = 0; cbits <= (minhamming - 1) / 2; cbits++) {
                long validCodes = 0; // how many input codes will be mapped to a single valid code?
                // it's the number of input codes that have 0 errors, 1 error, 2 errors, ..., cbits errors.
                for (int i = 0; i <= cbits; i++)
                    validCodes += choose(nbits, i);

                validCodes *= codes.length; // total number of codes

                System.out.printf("          %3d             %15.8f %%\n", cbits, (100.0 * validCodes) / (1L << nbits));
            }

            System.out.printf("\n    Generation time: %f s\n\n", (System.currentTimeMillis() - starttime) / 1000.0);

            System.out.printf("    Hamming distance between pairs of codes (accounting for rotation):\n\n");
            for (int i = 0; i < hds.length; i++) {
                System.out.printf("    %4d  %d\n", i, hds[i]);
            }

            System.out.printf("**/\n");

            System.out.printf("public class %s extends TagFamily\n", cname);
            System.out.printf("{\n");

            int maxLength = 8192;
            int numSubMethods = (maxLength + codes.length - 1) / maxLength;
            for (int i = 0; i < numSubMethods; i++) {
                System.out.printf("\tprivate static class ConstructCodes%d {\n", i);
                System.out.printf("\t\tprivate static long[] constructCodes() {\n");
                System.out.printf("\t\t\treturn new long[] { ");
                int jMax = Math.min(maxLength, codes.length - i * maxLength);
                for (int j = 0; j < jMax; j++) {
                    long w = codes[i * maxLength + j];
                    System.out.printf("0x%0" + ((int) Math.ceil(nbits / 4)) + "xL", w);
                    if (j == jMax - 1) {
                        System.out.printf(" };\n\t\t}\n\t}\n\n");
                    } else {
                        System.out.printf(", ");
                    }
                }
            }

            System.out.printf("\tprivate static long[] constructCodes() {\n");
            System.out.printf("\t\tlong[] codes = new long[%d];\n", codes.length);
            for (int i = 0; i < numSubMethods; i++) {
                System.out.printf("\t\tSystem.arraycopy(ConstructCodes%d.constructCodes(), 0, codes, %d, %d);\n",
                        i, i * maxLength, Math.min(maxLength, codes.length - i * maxLength));
            }
            System.out.printf("\t\treturn codes;\n");
            System.out.printf("\t}\n\n");


            System.out.printf("\tpublic %s()\n", cname);
            System.out.printf("\t{\n");
            System.out.printf("\t\tsuper(ImageLayout.Factory.createFromString(\"%s\", \"%s\"), %d, constructCodes());\n",
                    layout.getName(), layout.getDataString(), minhamming);
            System.out.printf("\t}\n");
            System.out.printf("}\n");
            System.out.printf("\n");
        }
    }

    static long choose(int n, int c)
    {
        long v = 1;
        for (int i = 0; i < c; i++)
            v *= (n-i);
        for (int i = 1; i <= c; i++)
            v /= i;
        return v;
    }

    /** Compute the hamming distance between two longs. **/
    public static final int hammingDistance(long a, long b)
    {
        return popCount2(a^b);
    }

    public static final boolean hammingDistanceAtLeast(long a, long b, int minval)
    {
        long w = a^b;

        int count = 0;

        while (w != 0) {
            count += popCountTable[(int) (w&(popCountTable.length-1))];
            if (count >= minval)
                return true;

            w >>= popCountTableShift;
        }

        return false;
    }

    /** How many bits are set in the long? **/
    static final int popCountReal(long w)
    {
        return Long.bitCount(w);
    }


    static final int popCountTableShift = 16;
    static final byte[] popCountTable = new byte[1<<popCountTableShift];
    static {
        for (int i = 0; i < popCountTable.length; i++) {
            popCountTable[i] = (byte) popCountReal(i);
        }
    }

    public static final int popCount2(long w)
    {
        int count = 0;

        while (w != 0) {
            count += popCountTable[(int) (w&(popCountTable.length-1))];
            w >>= popCountTableShift;
        }
        return count;
    }
}
