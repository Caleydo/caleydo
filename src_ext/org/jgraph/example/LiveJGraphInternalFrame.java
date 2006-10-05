package org.jgraph.example;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import org.jgraph.graph.DefaultGraphCell;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;

public class LiveJGraphInternalFrame extends JInternalFrame {
	private DefaultGraphCell _graphCell = null;

	public LiveJGraphInternalFrame() {
		setResizable(true);
		setFrameIcon(null);
		setClosable(true);
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
	}

	public void create() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		JScrollPane sp = new JScrollPane(new JTable(2, 3));
		mainPanel.add(sp, BorderLayout.CENTER);
		getContentPane().add(mainPanel);
		pack();
		setSize(320, 320);
		setVisible(true);
	}

	public String toString() {
		return ("");
	}

	public void setGraphCell(DefaultGraphCell graphCell) {
		_graphCell = graphCell;
	}

	public DefaultGraphCell getGraphCell() {
		return (_graphCell);
	}
}