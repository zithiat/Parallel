package exercises;

public class ParallelTask implements Runnable {

	private int num;
	private int tid;
	
	public ParallelTask(int tid, int num) {
		this.tid = tid;
		this.num = num;
	}
	
	@Override
	public void run() {
		System.out.println("Thread " + tid + " with " + num);
		MyParallelStack mps = new MyParallelStack();
		for (int i = 0; i < num; i++) {
			mps.push(i);
		}
		
		for (int i = 0; i < num; i++) {
			mps.pop();
		}
	}
}
