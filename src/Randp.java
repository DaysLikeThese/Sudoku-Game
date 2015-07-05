public class Randp {
	int size;
	int numsLeft;
	int[] numArray;

	public Randp(int n) {
		size = n;
		numsLeft = size;

		numArray = new int[size];
		for(int i = 0; i < size; i++) {
			numArray[i] = i + 1;
		}
	}

	public int nextInt() {

		if(numsLeft > 0) {
			int pos = (int) (Math.random() * numsLeft);
			int temp = numArray[pos];
			numArray[pos] = numArray[numsLeft - 1];
			numsLeft--;
			return temp;
		} else {
			return 0;
		}
	}

}