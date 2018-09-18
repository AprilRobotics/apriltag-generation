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

/** Generic class for all tag encoding families **/
public class TagFamily
{
    /** What is the minimum hamming distance between any two codes
     * (accounting for rotational ambiguity? The code can recover
     * (minHammingDistance-1)/2 bit errors.
     **/
    public final int minimumHammingDistance;

    /** The error recovery value determines our position on the ROC
     * curve. We will report codes that are within errorRecoveryBits
     * of a valid code. Small values mean greater rejection of bogus
     * tags (but false negatives). Large values mean aggressive
     * reporting of bad tags (but with a corresponding increase in
     * false positives).
     **/
    //public int errorRecoveryBits = 1;

    /** The array of the codes. The id for a code is its index. **/
    private final long codes[];
    private final ImageLayout layout;

    static long upgradeCode(long oldCode, int[][] bitLocations, int size) {
        long code = 0;

        for (int i = 0; i < bitLocations.length; i++) {
            code = code << 1;
            boolean val = (oldCode & (1L << (size - bitLocations[i][0] + (size - bitLocations[i][1])*size))) > 0;
            if (val) {
                code |= 1;
            }
        }

        return code;
    }

    static long[] upgradeCodes(long[] oldCodes, int size) {
        int bitLocations[][] = LayoutUtil.getClassicLayout(size + 4).getBitLocations();
        long fixed_codes[] = new long[oldCodes.length];
        for (int i = 0; i < oldCodes.length; i++) {
            fixed_codes[i] = upgradeCode(oldCodes[i], bitLocations, size);
        }
        return fixed_codes;
    }

    /**
     * Constructor for tags generated with previous AprilTag versions.
     */
    public TagFamily(int area, int minimumHammingDistance, long codes[]) {
        this(LayoutUtil.getClassicLayout((int) Math.sqrt(area) + 4), minimumHammingDistance, upgradeCodes(codes, (int) Math.sqrt(area)));
    }

    /** The codes array is not copied internally and so must not be
     * modified externally. **/
    public TagFamily(ImageLayout layout, int minimumHammingDistance, long codes[])
    {
        this.layout = layout;

        this.minimumHammingDistance = minimumHammingDistance;
        this.codes = codes;
    }

    /**
     * Assuming we are draw the image one quadrant at a time, what would the rotated image look like?
     * Special care is taken to handle the case where there is a middle pixel of the image.
     */
    public static long rotate90(long w, int numBits)
    {
        int p = numBits;
        int l = 0;
        if (numBits % 4 == 1) {
            p = numBits - 1;
            l = 1;
        }
        w = ((w >> l) << (p/4 + l)) | (w >> (3 * p/ 4 + l) << l) | (w & l);
        w &= ((1L << numBits) - 1);
        return w;
    }

    /** Compute the hamming distance between two longs. **/
    /*public static final int hammingDistance(long a, long b)
    {
        return popCount(a^b);
    }*/

    /** How many bits are set in the long? **/
    /*static final int popCountReal(long w)
    {
        int cnt = 0;
        while (w != 0) {
            w &= (w-1);
            cnt++;
        }
        return cnt;
    }*/

    /*
    static final int popCountTableShift = 12;
    static final byte[] popCountTable = new byte[1<<popCountTableShift];
    static {
        for (int i = 0; i < popCountTable.length; i++)
            popCountTable[i] = (byte) popCountReal(i);
    }*/

    /*
    public static final int popCount(long w)
    {
        int count = 0;

        while (w != 0) {
            count += popCountTable[(int) (w&(popCountTable.length-1))];
            w >>= popCountTableShift;
        }
        return count;
    }*/

    public long[] getCodes() {
        return codes;
    }

    // TODO improve this. We need different names for different layouts.
    public String getFilePrefix() {
        return String.format("tag%02d_%02d",
                layout.getNumBits(),
                minimumHammingDistance);
    }

    public ImageLayout getLayout() {
        return layout;
    }

    /** Given an observed tag with code 'rcode', try to recover the
     * id. The corresponding fields of TagDetection will be filled
     * in. **/
    /*public void decode(TagDetection det, long rcode)
    {
        int  bestid = -1;
        int  besthamming = Integer.MAX_VALUE;
        int  bestrotation = 0;
        long bestcode = 0;

        long rcodes[] = new long[4];

        rcodes[0] = rcode;
        rcodes[1] = rotate90(rcodes[0], d);
        rcodes[2] = rotate90(rcodes[1], d);
        rcodes[3] = rotate90(rcodes[2], d);

        for (int id = 0; id < codes.length; id++) {

            for (int rot = 0; rot < rcodes.length; rot++) {
                int thishamming = hammingDistance(rcodes[rot], codes[id]);
                if (thishamming < besthamming) {
                    besthamming = thishamming;
                    bestrotation = rot;
                    bestid = id;
                    bestcode = codes[id];
                }
            }
        }

        det.id = bestid;
        det.hammingDistance = besthamming;
        det.rotation = bestrotation;
        det.good = (det.hammingDistance <= errorRecoveryBits);
        det.obsCode = rcode;
        det.code = bestcode;
    }*/



    /*
    public void printHammingDistances()
    {
        int hammings[] = new int[d*d+1];

        for (int i = 0; i < codes.length; i++) {
            long r0 = codes[i];
            long r1 = rotate90(r0, d);
            long r2 = rotate90(r1, d);
            long r3 = rotate90(r2, d);

            for (int j = i+1; j < codes.length; j++) {

                int d = Math.min(Math.min(hammingDistance(r0, codes[j]),
                                          hammingDistance(r1, codes[j])),
                                 Math.min(hammingDistance(r2, codes[j]),
                                          hammingDistance(r3, codes[j])));

                hammings[d]++;
            }
        }

        for (int i = 0; i < hammings.length; i++)
            System.out.printf("%10d  %10d\n", i, hammings[i]);
    }
    */
}
