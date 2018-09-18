package april.tag;

/** Tag family with 38 distinct codes.
    bits: 21,  minimum hamming: 7,  minimum complexity: 10

    Max bits corrected       False positive rate
            0                  0.00181198 %
            1                  0.03986359 %
            2                  0.42037964 %
            3                  2.83031464 %

    Generation time: 0.937000 s

    Hamming distance between pairs of codes (accounting for rotation):

       0  0
       1  0
       2  0
       3  0
       4  0
       5  0
       6  0
       7  164
       8  255
       9  162
      10  78
      11  31
      12  10
      13  3
      14  0
      15  0
      16  0
      17  0
      18  0
      19  0
      20  0
      21  0
**/
public class TagCircle21h7 extends TagFamily
{
	private static class ConstructCodes0 {
		private static long[] constructCodes() {
			return new long[] { 0x157863L, 0x47e28L, 0x1383edL, 0x0953cL, 0xda68bL, 0x1cac50L, 0xbb215L, 0x16ceeeL, 0x5d4b3L, 0x1ff751L, 0xefd16L, 0x72b3eL, 0x163103L, 0x106e56L, 0x1996b9L, 0xc0234L, 0x624d2L, 0x1fa985L, 0x344a5L, 0x762fbL, 0x19e92bL, 0x43755L, 0x1a4f4L, 0x10fad8L, 0x01b52L, 0x17e59fL, 0xe6f70L, 0xed47aL, 0xc9931L, 0x14df2L, 0xa06f1L, 0xe5041L, 0x12ec03L, 0x16724eL, 0xaf1a5L, 0x8a8acL, 0x15b39L, 0x1ec1e3L };
		}
	}

	private static long[] constructCodes() {
		long[] codes = new long[38];
		System.arraycopy(ConstructCodes0.constructCodes(), 0, codes, 0, 38);
		return codes;
	}

	public TagCircle21h7()
	{
		super(ImageLayout.Factory.createFromString("Circle", "xxxdddxxxxbbbbbbbxxbwwwwwbxdbwdddwbddbwdddwbddbwdddwbdxbwwwwwbxxbbbbbbbxxxxdddxxx"), 7, constructCodes());
	}
}

