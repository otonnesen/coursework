public class charArrayToString {
	public static void main(String[] args) {
		char[] a = new char[4];
		a[0] = 'a';
		a[1] = 'b';
		a[3] = 'c';
		String b = "";
		for(int i = 0; i < a.length; i++) {
			if(a[i]!='\u0000') {
				System.out.println(a[i]);
				b += a[i];
			}
		}
		System.out.println(b);
	}
}
