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
