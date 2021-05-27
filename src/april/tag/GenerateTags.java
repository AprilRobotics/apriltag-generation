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

import java.io.File;
import java.io.IOException;

public class GenerateTags {
    public static void main(String args[])
    {
        if (args.length < 2 || args.length > 3) {
            System.out.printf("Usage: <tagclass> <outputdir> [<scalefactor>]\n");
            System.out.printf("Example: april.tag.Tag25h11 /tmp/tag25h11\n");
            return;
        }

        String cls = args[0];
        String dirpath = args[1] + "/";
        int scaleFactor = 1;
        if (args.length > 2) {
            scaleFactor = Integer.parseInt(args[2]);
            if (scaleFactor < 1) {
                System.err.println("Scale factor must be greater or equals 1");
                return;
            }
        }

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
            renderer.writeAllImages(dirpath, tagFamily.getFilePrefix(), scaleFactor);
            renderer.writeAllImagesPostScript(dirpath+"alltags.ps");
        } catch (IOException ex) {
            System.out.println("ex: "+ex);
        }
    }
}
