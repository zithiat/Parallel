package exercises;

import java.util.LinkedList;
import java.util.List;

public class MyParallelStack {

private List<Integer> mystack;
	
	public MyParallelStack() {
		this.mystack = new LinkedList<Integer>();
	}
	
	public void push(int x) {
		this.mystack.add(x);
	}
	
	public int pop() {
		int res = 0;
		int index = this.mystack.size();
		res = this.mystack.get(index - 1);
		this.mystack.remove(index - 1);
		return res;
	}
	
	public void printStack() {
		for (int i = this.mystack.size() - 1; i >= 0; i--) {
			System.out.print(this.mystack.get(i) + ((i == 0) ? "\n" : " "));
		}
	}
	
	public void clear() {
		this.mystack = new LinkedList<Integer>();
	}
}
