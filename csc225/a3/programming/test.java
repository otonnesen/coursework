import java.util.HashMap;

public class test {
	public static void main(String[] args) {
		// assert Ladder.isNeighbor("test", "tust");
		// assert Ladder.isNeighbor("test", "tert");
		// assert !Ladder.isNeighbor("bigmoney", "bugmaney");
		// assert !Ladder.isNeighbor("what", "tahw");
		// assert Ladder.isNeighbor("g", "h");

		for (String s: Ladder.getBuckets("test")) {
			System.out.printf("%s\n", s);
		}
	}
}
