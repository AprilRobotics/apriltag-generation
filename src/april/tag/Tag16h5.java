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

/** Tag family with 30 distinct codes.
    bits: 16,  minimum hamming: 5,  minimum complexity: 5

    Max bits corrected       False positive rate
            0                    0.045776 %
            1                    0.778198 %
            2                    6.271362 %

    Generation time: 0.309000 s

    Hamming distance between pairs of codes (accounting for rotation):

       0  0
       1  0
       2  0
       3  0
       4  0
       5  120
       6  172
       7  91
       8  33
       9  13
      10  6
      11  0
      12  0
      13  0
      14  0
      15  0
      16  0
**/
public class Tag16h5 extends TagFamily
{
	public Tag16h5()
	{
		super(16, 5, new long[] { 0x231bL, 0x2ea5L, 0x346aL, 0x45b9L, 0x79a6L, 0x7f6bL, 0xb358L, 0xe745L, 0xfe59L, 0x156dL, 0x380bL, 0xf0abL, 0x0d84L, 0x4736L, 0x8c72L, 0xaf10L, 0x093cL, 0x93b4L, 0xa503L, 0x468fL, 0xe137L, 0x5795L, 0xdf42L, 0x1c1dL, 0xe9dcL, 0x73adL, 0xad5fL, 0xd530L, 0x07caL, 0xaf2eL });
	}
}
