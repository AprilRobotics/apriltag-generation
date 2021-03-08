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

public class TagToC
{
    public static void main(String args[]) throws IOException
    {
        String cls = args[0];

        TagFamily tf = (TagFamily) april.util.ReflectUtil.createObject(cls);
        if (tf == null) {
            System.out.println("Could not find class.");
            return;
        }

        String indent = "   ";

        String cname = String.format("tag%s%dh%d.c", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance);
        String text_name = String.format("tag%s%dh%d", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance);

        BufferedWriter outs = new BufferedWriter(new FileWriter(cname));

        outs.write(String.format("#include <stdlib.h>\n"));
        outs.write(String.format("#include \"tag%s%dh%d.h\"\n\n", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));

        int num_codes = Math.min(tf.getCodes().length, 65535);
        outs.write(String.format("static uint64_t codedata[%d] = {\n", num_codes));
        for (int i = 0; i < num_codes; i++) {
            outs.write(String.format("%s0x%016xUL,\n", indent, tf.getCodes()[i]));
        }
        outs.write(String.format("};\n"));

        outs.write(String.format("apriltag_family_t *tag%s%dh%d_create()\n", tf.getLayout().getName(),tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("{\n"));
        outs.write(String.format("%sapriltag_family_t *tf = calloc(1, sizeof(apriltag_family_t));\n", indent));
        outs.write(String.format("%stf->name = strdup(\"%s\");\n", indent, text_name));
        outs.write(String.format("%stf->h = %d;\n", indent, tf.minimumHammingDistance));
        outs.write(String.format("%stf->ncodes = %d;\n", indent, num_codes));
        outs.write(String.format("%stf->codes = codedata;\n", indent));
        outs.write(String.format("%stf->nbits = %d;\n", indent, tf.getLayout().getNumBits()));
        outs.write(String.format("%stf->bit_x = calloc(%d, sizeof(uint32_t));\n", indent, tf.getLayout().getNumBits()));
        outs.write(String.format("%stf->bit_y = calloc(%d, sizeof(uint32_t));\n", indent, tf.getLayout().getNumBits()));
        int[][] locations = tf.getLayout().getBitLocations();
        for (int i = 0; i < locations.length; i++) {
            outs.write(String.format("%stf->bit_x[%d] = %d;\n", indent, i, locations[i][0]));
            outs.write(String.format("%stf->bit_y[%d] = %d;\n", indent, i, locations[i][1]));
        }
        outs.write(String.format("%stf->width_at_border = %d;\n", indent, tf.getLayout().getBorderWidth()));
        outs.write(String.format("%stf->total_width = %d;\n", indent, tf.getLayout().getSize()));
        outs.write(String.format("%stf->reversed_border = %s;\n", indent, tf.getLayout().isReversedBorder()));
        outs.write(String.format("%sreturn tf;\n", indent));
        outs.write(String.format("}\n"));
        outs.write(String.format("\n"));
        outs.write(String.format("void tag%s%dh%d_destroy(apriltag_family_t *tf)\n", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("{\n"));
        outs.write(String.format("%sfree(tf->bit_x);\n", indent));
        outs.write(String.format("%sfree(tf->bit_y);\n", indent));
        outs.write(String.format("%sfree(tf->name);\n", indent));
        outs.write(String.format("%sfree(tf);\n", indent));
        outs.write(String.format("}\n"));
        outs.flush();
        outs.close();



        String hname = String.format("tag%s%dh%d.h", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance);
        outs = new BufferedWriter(new FileWriter(hname));
        outs.write(String.format("#ifndef _TAG%s%dH%d\n", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("#define _TAG%s%dH%d\n\n",tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("#include \"apriltag.h\"\n\n"));

        outs.write(String.format("#ifdef __cplusplus\nextern \"C\" {\n#endif\n\n"));

        outs.write(String.format("apriltag_family_t *tag%s%dh%d_create();\n", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("void tag%s%dh%d_destroy(apriltag_family_t *tf);\n\n", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("#ifdef __cplusplus\n}\n#endif\n\n"));
        outs.write(String.format("#endif\n"));
        outs.flush();
        outs.close();
    }
}
