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

/** Tag family with 35 distinct codes.
    bits: 25,  minimum hamming: 9,  minimum complexity: 8

    Max bits corrected       False positive rate
            0                    0.000104 %
            1                    0.002712 %
            2                    0.034004 %
            3                    0.273913 %
            4                    1.593411 %

    Generation time: 31.283000 s

    Hamming distance between pairs of codes (accounting for rotation):

       0  0
       1  0
       2  0
       3  0
       4  0
       5  0
       6  0
       7  0
       8  0
       9  156
      10  214
      11  120
      12  64
      13  29
      14  11
      15  1
      16  0
      17  0
      18  0
      19  0
      20  0
      21  0
      22  0
      23  0
      24  0
      25  0
**/
public class Tag25h9 extends TagFamily
{
	public Tag25h9()
	{
		super(25, 9, new long[] { 0x155cbf1L, 0x1e4d1b6L, 0x17b0b68L, 0x1eac9cdL, 0x12e14ceL, 0x3548bbL, 0x7757e6L, 0x1065dabL, 0x1baa2e7L, 0xdea688L, 0x81d927L, 0x51b241L, 0xdbc8aeL, 0x1e50e19L, 0x15819d2L, 0x16d8282L, 0x163e035L, 0x9d9b81L, 0x173eec4L, 0xae3a09L, 0x5f7c51L, 0x1a137fcL, 0xdc9562L, 0x1802e45L, 0x1c3542cL, 0x870fa4L, 0x914709L, 0x16684f0L, 0xc8f2a5L, 0x833ebbL, 0x59717fL, 0x13cd050L, 0xfa0ad1L, 0x1b763b0L, 0xb991ceL });
	}
}
