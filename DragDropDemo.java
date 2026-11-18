package guipractice;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;

public class DragDropDemo extends JFrame {

    private DefaultListModel<String> sourceModel;
    private DefaultListModel<String> targetModel;
    private JList<String> sourceList;
    private JList<String> targetList;

    public DragDropDemo() {
        setTitle("Drag and Drop Demo");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2, 10, 10));

        // ---------- Source list (left) ----------
        sourceModel = new DefaultListModel<>();
        sourceModel.addElement("Apple");
        sourceModel.addElement("Banana");
        sourceModel.addElement("Cherry");
        sourceModel.addElement("Mango");
        sourceModel.addElement("Orange");

        sourceList = new JList<>(sourceModel);
        sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourceList.setDragEnabled(true); // MUST enable for DnD to start
        sourceList.setTransferHandler(new ListTransferHandler(true));

        // ---------- Target list (right) ----------
        targetModel = new DefaultListModel<>();
        targetList = new JList<>(targetModel);
        targetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        targetList.setTransferHandler(new ListTransferHandler(false));

        // Wrap in scroll panes + labeled panels
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Drag FROM here", SwingConstants.CENTER), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(sourceList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Drop HERE", SwingConstants.CENTER), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(targetList), BorderLayout.CENTER);

        add(leftPanel);
        add(rightPanel);
    }

    // ---------- Custom TransferHandler ----------
    private class ListTransferHandler extends TransferHandler {

        private final boolean isSource;

        public ListTransferHandler(boolean isSource) {
            this.isSource = isSource;
        }

        // 1. Which actions are allowed?
        @Override
        public int getSourceActions(JComponent c) {
            return isSource ? MOVE : NONE;
        }

        // 2. When a drag starts on the source list: package the selected item
        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<String> list = (JList<String>) c;
            String selected = list.getSelectedValue();
            return new java.awt.datatransfer.StringSelection(selected);
        }

        // 3. Called after a successful move (source side) — remove the item
        @Override
        protected void exportDone(JComponent source, java.awt.datatransfer.Transferable data, int action) {
            if (action == MOVE) {
                JList<String> list = (JList<String>) source;
                DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
                int index = list.getSelectedIndex();
                if (index != -1) {
                    model.remove(index);
                }
            }
        }

        // 4. Can the target accept this drop?
        @Override
        public boolean canImport(TransferSupport support) {
            return !isSource && support.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor);
        }

        // 5. Perform the drop on the target
        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) return false;
            try {
                String data = (String) support.getTransferable()
                        .getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                JList<String> list = (JList<String>) support.getComponent();
                DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
                model.addElement(data);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DragDropDemo().setVisible(true));
    }
}