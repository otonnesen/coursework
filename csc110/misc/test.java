public class test {
	public static void main(String[] args) {
		String x="ACGT";
		String[] y = {"ACCGT", "AACGTT", "GACT", "AC", "GTAC"};
		System.out.println(findFreqWithMutations(x,y));
	}
	
	public static int findFreqWithMutations(String target, String[] seqArray) {

		int sum = 0;
		char x;
		String y;
		for(int i = 0; i < seqArray.length; i++) {

			x = seqArray[i].charAt(0);
			y="";
			for(int j = 0; j < seqArray[i].length(); j++) {
				if(x!=seqArray[i].charAt(j)) {
					y += x;
					x = seqArray[i].charAt(j);
				}
				
			}
			y += seqArray[i].charAt(seqArray[i].length()-1);
			if(y.equals(target)){
				sum++;
			}
		}
		return sum;
	}

}
