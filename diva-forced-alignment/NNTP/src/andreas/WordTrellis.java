package andreas;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class WordTrellis {

	protected WordTrellisNode lineStart;
	protected WordTrellisNode lineEnd;
	protected LinkedList<WordHistory> wordHistories;
	
	public WordTrellis() {
		lineStart = new WordTrellisNode(null, -1, 0f, 0f);
		lineEnd = null;
		wordHistories = new LinkedList<WordHistory>();
	}
	
	public WordHistory getStartHistory() {
		WordHistory startHistory = new WordHistory(-1);
		startHistory.add(lineStart);
		return startHistory;
	}
	
	public WordTrellisNode getLineEnd() {
		return lineEnd;
	}
	
	public WordHistory getHistory(int time) {
		return wordHistories.get(time);
	}
	
	public void addHistory(WordHistory history) {
		wordHistories.add(history);
	}
	
	public void finalize() {
		int endTime = wordHistories.size()-1;
		lineEnd = new WordTrellisNode(null, endTime+1, 0f);
		WordHistory lastHistory = wordHistories.get(endTime);
		if (lastHistory.size() == 0) {
			int time = endTime-1;
			while (time >= 0 && lastHistory.size() == 0) {
				lastHistory = wordHistories.get(time);
				time--;
			}
			System.out.println("WARNING partial decoding " + (time+1) + "/" + (endTime+1));
		}
		Iterator<WordTrellisNode> itr = lastHistory.iterator();
		while (itr.hasNext()) {
			lineEnd.addNullEdge(itr.next());
		}
	}
	
	public void uncompress(ObservationSequence os) {
		int time;
		WordHistory history;
		WordTrellisNode node;
		for (int i=0; i < wordHistories.size(); i++) {
			history = wordHistories.get(i);
			time = os.uncompressedPosition(history.getTime());
			history.setTime(time);
			Iterator<WordTrellisNode> itr = history.iterator();
			while (itr.hasNext()) {
				node = itr.next();
				node.setTime(time);
			}
		}
	}

	public void writeLattice(PrintWriter out, MainParams params, int idx) {
		lineStart.setTime(0);
		lineEnd.setTime(wordHistories.getLast().getTime());
		LinkedList<WordTrellisNode> nodes = getLatticeNodes();
		
		Iterator<WordTrellisNode> itr;
		Iterator<WordTrellisEdge> itr2;
		int numNodes = 0;
		int numEdges = 0;
		itr = nodes.iterator();
		while (itr.hasNext()) {
			numNodes += 1;
			numEdges += itr.next().edges.size();
		}
		
		// header

		out.println("VERSION=1.0");
		out.println("UTTERANCE=" + params.getId(idx));
		if (params.get("obsLogBase").compareTo("logTen") == 0) {
			out.println("base=10");
		}
//		out.println("lmname=" + params.get("fileLanguageModel"));
		out.println("lmscale=" + String.format("%-10.2f", params.getFloat("gsf")) + "wdpenalty=" + String.format("%.2f", params.getFloat("wip")));
		out.println("acscale=1.00");
//		out.println("vocab=" + params.get("fileSpelling"));
		out.println("N=" + String.format("%-5d", numNodes) + "L=" + numEdges);
		
		// nodes
		
		int latticeNodeId = 0;
		itr = nodes.iterator();
		while (itr.hasNext()) {
			WordTrellisNode node = itr.next();
			node.setLatticeId(latticeNodeId);
			out.println(node.strLattice());
			latticeNodeId++;
		}
		
		// edges
		
		int latticeEdgeId = 0;
		itr = nodes.iterator();
		while (itr.hasNext()) {
			itr2 = itr.next().iterator();
			while (itr2.hasNext()) {
				WordTrellisEdge edge = itr2.next();
				edge.setLatticeId(latticeEdgeId);
				out.println(edge.strLattice());
				latticeEdgeId++;
			}
		}
	}
	
	public LinkedList<WordTrellisNode> getLatticeNodes() {
		LinkedList<WordTrellisNode> open = new LinkedList<WordTrellisNode>();
		LinkedList<WordTrellisNode> closed = new LinkedList<WordTrellisNode>();
		open.add(lineEnd);
		lineEnd.setVisited();
		while (!open.isEmpty()) {
			WordTrellisNode node = open.removeFirst();
			closed.add(node);
			Iterator<WordTrellisEdge> itr = node.iterator();
			while (itr.hasNext()) {
				WordTrellisNode newNode = itr.next().getGoalNode();
				if (!newNode.isVisited()) {
					open.add(newNode);
					newNode.setVisited();
				}
			}
		}
		Collections.sort(closed, new Comparator<WordTrellisNode>() {
			public int compare(WordTrellisNode o1, WordTrellisNode o2) {
				if (o1.getTime() != o2.getTime()) {
					return new Integer(o1.getTime()).compareTo(new Integer(o2.getTime()));
				}
		        return o1.getLabel().compareTo(o2.getLabel());
		    }
		});
		return closed;
	}
	
}
