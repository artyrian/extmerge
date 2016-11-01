package ru.artyrian;

/**
 * Created by artyrian on 11/1/16.
 */
public class Item {
	private Integer fid;
	private String line;

	public Item(String line) {
		this.fid = Integer.parseInt(line.split(";")[0]);
		this.line = line;
	}

	public Item(Integer fid, String line) {
		this.fid = fid;
		this.line = line;
	}

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	@Override
	public String toString() {
		return line;
	}
}
