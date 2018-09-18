package april.tag;

import java.io.File;
import java.io.IOException;

public class GenerateTags {
    public static void main(String args[])
    {
        if (args.length != 2) {
            System.out.printf("Usage: <tagclass> <outputdir>\n");
            System.out.printf("Example: april.tag.Tag25h11 /tmp/tag25h11\n");
            return;
        }

        String cls = args[0];
        String dirpath = args[1] + "/";

        TagFamily tagFamily = (TagFamily) april.util.ReflectUtil.createObject(cls);
        if (tagFamily == null) {
            System.err.println("Tag family not found.");
            return;
        }

        TagRenderer renderer = new TagRenderer(tagFamily);

        try {
            File f = new File(dirpath);
            if (!f.exists())
                f.mkdirs();

            renderer.writeAllImagesMosaic(dirpath+"mosaic.png");
            renderer.writeAllImages(dirpath, tagFamily.getFilePrefix());
            renderer.writeAllImagesPostScript(dirpath+"alltags.ps");
        } catch (IOException ex) {
            System.out.println("ex: "+ex);
        }
    }
}
