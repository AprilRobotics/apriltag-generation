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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TagRenderer {

    private final ImageLayout layout;
    private final long[] codes;
    private final String tagFamilyName;

    TagRenderer(TagFamily tagFamily) {
        this.codes = tagFamily.getCodes();
        this.layout = tagFamily.getLayout();
        this.tagFamilyName = tagFamily.getClass().getName();
    }

    public BufferedImage getAllImagesMosaic()
    {
        ArrayList<BufferedImage> ims = new ArrayList<BufferedImage>();

        for (int i = 0; i < codes.length; i++) {
            BufferedImage im = makeImage(i);
            ims.add(im);
        }

        int mosaicWidth = (int) Math.sqrt(ims.size());
        int mosaicHeight = ims.size() / mosaicWidth + (ims.size() % mosaicWidth != 0 ? 1 : 0);
        int imageSize = layout.getSize() + 1;

        BufferedImage im = new BufferedImage(mosaicWidth*imageSize - 1, mosaicHeight*imageSize - 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = im.createGraphics();

        for (int y = 0; y < mosaicHeight; y++) {
            for (int x = 0; x < mosaicWidth; x++) {
                int id = y * mosaicWidth + x;
                if (id >= codes.length)
                    continue;

                g.drawImage(ims.get(id), x * imageSize, y * imageSize, null);
            }
        }

        return im;
    }

    public void writeAllImagesMosaic(String filepath) throws IOException
    {
        BufferedImage im = getAllImagesMosaic();

        ImageIO.write(im, "png", new File(filepath));
    }

    public void writeAllImagesPostScript(String filepath) throws IOException
    {
        int sz = layout.getSize();

        BufferedWriter outs = new BufferedWriter(new FileWriter(filepath));

        outs.write("/pagewidth 8.5 72 mul def                      \n" +
                "/pageheight 11 72 mul def                      \n" +

                "/maketag                                       \n" +
                "{                                              \n" +
                "  /img exch def                                \n" +
                "  /name exch def                               \n" +
                "  gsave                                        \n" +
                "  pagewidth 2 div pageheight 2 div translate   \n" +
                "  0 0 moveto                                   \n" +
                "  1.0 pagewidth mul dup scale                  \n" +
                "  1 -1 scale                                   \n" +
                "  -.5 -.5 translate                            \n" +
                "  " + sz + " " + sz + " 2 [ " + sz + " 0 0 " + sz + " 0 0 ] { img } image \n" +
                "  0 setlinewidth .5 setgray [0.002 0.01] 0 setdash \n" +
                "  0 0 moveto 1 0 lineto 1 1 lineto 0 1 lineto  \n" +
                "  closepath stroke                             \n" +
                "  grestore                                     \n" +
                "  gsave                                        \n" +
                "  pagewidth 2 div 72 translate                 \n" +
                "  /Helvetica-Bold findfont 20 scalefont setfont \n" +
                "  name                                         \n" +
                "  dup stringwidth pop -.5 mul 0 moveto         \n" +
                "  show                                         \n" +
                "  grestore                                     \n" +
                "  showpage                                     \n" +
                "} def                                          \n");

        for (int id = 0; id < codes.length; id++) {
            BufferedImage im = makeImage(id);

            // convert image into a postscript string
            //int width = im.getWidth(), height = im.getHeight();

            String imgdata = "";

            for (int y = 0; y < sz; y++) {
                long v = 0;
                int vlen = 0;

                for (int x = 0; x < sz; x++) {
                    int b;
                    switch (im.getRGB(x,y)) {
                        case ImageLayout.BLACK:
                            b = 0;
                            break;
                        case ImageLayout.WHITE:
                            b = 3;
                            break;
                        case ImageLayout.TRANSPARENT:
                            b = 2;
                            break;
                        default:
                            throw new RuntimeException("Unknown color.");
                    }
                    v = (v<<2) + b; vlen += 2;
                }

                // pad to a byte boundary.
                while ((vlen%8) != 0) {
                    v = (v<<1) | 0; vlen++;
                }
                imgdata += String.format("%0"+(vlen/4)+"x", v);
            }
            outs.write("(" + tagFamilyName + ", id = "+id+") <"+imgdata+"> maketag\n");
        }

        outs.close();
    }

    /** Generate all valid tags, writing them as PNGs in the specified
     * directory.  The files will be named tag##_##_####.png, where
     * the first block is nbits, the second block is hamming distance,
     * and the final block is the id.
     **/
    public void writeAllImages(String dirpath, String prefix) throws IOException
    {
        for (int i = 0; i < codes.length; i++) {
            BufferedImage im = makeImage(i);
            String fname = String.format("%s_%05d.png",
                    prefix,
                    i);
            try {
                ImageIO.write(im, "png", new File(dirpath + "/" + fname));
            } catch (IOException ex) {
                System.out.println("ex: "+ex);
            }
        }
    }

    public BufferedImage makeImage(int id)
    {
        long v = codes[id];
        return layout.renderToImage(v);
    }
}
