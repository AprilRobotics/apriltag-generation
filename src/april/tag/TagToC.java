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

        BufferedWriter outs = new BufferedWriter(new FileWriter(cname));

        outs.write(String.format("#include <stdlib.h>\n"));
        outs.write(String.format("#include \"apriltag.h\"\n\n"));
        outs.write(String.format("apriltag_family_t *tag%s%dh%d_create()\n", tf.getLayout().getName(),tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("{\n"));
        outs.write(String.format("%sapriltag_family_t *tf = calloc(1, sizeof(apriltag_family_t));\n", indent));
        outs.write(String.format("%stf->h = %d;\n", indent, tf.minimumHammingDistance));
        outs.write(String.format("%stf->ncodes = %d;\n", indent, tf.getCodes().length));
        outs.write(String.format("%stf->codes = calloc(%d, sizeof(uint64_t));\n", indent, tf.getCodes().length));
        for (int i = 0; i < tf.getCodes().length; i++) {
            outs.write(String.format("%stf->codes[%d] = 0x%016xUL;\n", indent, i, tf.getCodes()[i]));
        }
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
        outs.write(String.format("%sfree(tf->codes);\n", indent));
        outs.write(String.format("%sfree(tf->bit_x);\n", indent));
        outs.write(String.format("%sfree(tf->bit_y);\n", indent));
        outs.write(String.format("%sfree(tf);\n", indent));
        outs.write(String.format("}\n"));
        outs.flush();
        outs.close();



        String hname = String.format("tag%s%dh%d.h", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance);
        outs = new BufferedWriter(new FileWriter(hname));
        outs.write(String.format("#ifndef _TAG%s%dH%d\n", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("#define _TAG%s%dH%d\n\n",tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("apriltag_family_t *tag%s%dh%d_create();\n", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("void tag%s%dh%d_destroy(apriltag_family_t *tf);\n", tf.getLayout().getName(), tf.getLayout().getNumBits(), tf.minimumHammingDistance));
        outs.write(String.format("#endif"));
        outs.flush();
        outs.close();
    }
}
