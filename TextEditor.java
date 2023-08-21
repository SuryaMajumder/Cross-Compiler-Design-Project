import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;

public class TextEditor extends JFrame implements ActionListener 
{
    private JFrame frame;
    private JTextPane textPane;
    private UndoManager undoManager;
    private JTabbedPane tabbedPane;
    private JToolBar toolBar;
    private JFileChooser fileChooser;
    private JComboBox<String> fontBox, sizeBox, styleBox, themeBox;
    private JButton boldButton, italicButton, underlineButton, strikeThroughButton, compileButton;
    private JMenuItem windowsThemeItem, macThemeItem, linuxThemeItem, darkThemeItem, draculaThemeItem, metalThemeItem, motifThemeItem, nimbusThemeItem, nimbusLightThemeItem, hackingThemeItem, contrastThemeItem;
    private int tabCount = 0;
    
    public TextEditor() 
    {
        frame = new JFrame("Text Editor");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        textPane = new JTextPane();
        Font font = new Font("Serif", Font.PLAIN, 18);
        textPane.setFont(font);
        JScrollPane scrollPane = new JScrollPane(textPane);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        undoManager = new UndoManager();
        textPane.getDocument().addUndoableEditListener(undoManager);
        textPane.putClientProperty("UndoManager", undoManager);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Untitled " + tabCount++, scrollPane);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        // Selecting the new tab
        tabbedPane.setSelectedComponent(scrollPane);
        
        //Creating the Menubar
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        // Creating the file menu
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem newMenuItem = new JMenuItem("New Tab");
        newMenuItem.addActionListener(this);
        fileMenu.add(newMenuItem);
        
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(this);
        fileMenu.add(openMenuItem);
        
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(this);
        fileMenu.add(saveMenuItem);
        
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.addActionListener(this);
        fileMenu.add(saveAsMenuItem);
        
        JMenuItem exitMenuItem = new JMenuItem("Close Tab");
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);
        
        JMenuItem quitMenuItem = new JMenuItem("Exit");
        quitMenuItem.addActionListener(this);
        fileMenu.add(quitMenuItem);
        menuBar.add(fileMenu);
        
        // Creating the edit menu
        JMenu editMenu = new JMenu("Edit");
        
        JMenuItem cutMenuItem = new JMenuItem("Cut");
        cutMenuItem.addActionListener(this);
        editMenu.add(cutMenuItem);
        
        JMenuItem copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.addActionListener(this);
        editMenu.add(copyMenuItem);
        
        JMenuItem pasteMenuItem = new JMenuItem("Paste");
        pasteMenuItem.addActionListener(this);
        editMenu.add(pasteMenuItem);
        
        JMenuItem selectAllMenuItem = new JMenuItem("Select All");
        selectAllMenuItem.addActionListener(this);
        editMenu.add(selectAllMenuItem);
        
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoMenuItem.addActionListener(this);
        editMenu.add(undoMenuItem);
        
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoMenuItem.addActionListener(this);
        editMenu.add(redoMenuItem);
        menuBar.add(editMenu);
        
        // Creating the theme menu
        JMenu themeMenu = new JMenu("Theme");
        
        windowsThemeItem = new JMenuItem("Windows");
        windowsThemeItem.addActionListener(this);
        themeMenu.add(windowsThemeItem);
        
        macThemeItem = new JMenuItem("Mac");
        macThemeItem.addActionListener(this);
        themeMenu.add(macThemeItem);
        
        linuxThemeItem = new JMenuItem("Linux");
        linuxThemeItem.addActionListener(this);
        themeMenu.add(linuxThemeItem);
        
        darkThemeItem = new JMenuItem("Dark");
        darkThemeItem.addActionListener(this);
        themeMenu.add(darkThemeItem);
        
        draculaThemeItem = new JMenuItem("Dracula");
        draculaThemeItem.addActionListener(this);
        themeMenu.add(draculaThemeItem);
        
        metalThemeItem = new JMenuItem("Metal");
        metalThemeItem.addActionListener(this);
        themeMenu.add(metalThemeItem);
        
        motifThemeItem = new JMenuItem("Motif");
        motifThemeItem.addActionListener(this);
        themeMenu.add(motifThemeItem);
        
        nimbusThemeItem = new JMenuItem("Nimbus");
        nimbusThemeItem.addActionListener(this);
        themeMenu.add(nimbusThemeItem);
        
        nimbusLightThemeItem = new JMenuItem("Nimbus Light");
        nimbusLightThemeItem.addActionListener(this);
        themeMenu.add(nimbusLightThemeItem);
        menuBar.add(themeMenu);
        
        // Setting the default look and feel to the system default
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting default theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        
        // Creating the view menu
        JMenu viewMenu = new JMenu("View");
        
        hackingThemeItem = new JMenuItem("Hack View");
        hackingThemeItem.addActionListener(this);
        viewMenu.add(hackingThemeItem);
        
        contrastThemeItem = new JMenuItem("Contrast View");
        contrastThemeItem.addActionListener(this);
        viewMenu.add(contrastThemeItem);
        menuBar.add(viewMenu);
        
        
        
        fileChooser = new JFileChooser();
        
        // Creating the Toolbar
        toolBar = new JToolBar();
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);
        
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontBox = new JComboBox<String>(fonts);
        fontBox.addActionListener(this);
        toolBar.add(fontBox);
        
        sizeBox = new JComboBox<String>(new String[]{"8", "10", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"});
        sizeBox.addActionListener(this);
        toolBar.add(sizeBox);
        
        styleBox = new JComboBox<String>(new String[]{"Regular", "Bold", "Italic", "Bold Italic"});
        styleBox.addActionListener(this);
        toolBar.add(styleBox);
        
        themeBox = new JComboBox<String>(new String[]{"Light", "Dark", "Dracula", "Dracula White", "Geek"});
        themeBox.addActionListener(this);
        toolBar.add(themeBox);
        
        boldButton = new JButton(new ImageIcon("RTF_BOLD.gif"));
        boldButton.setToolTipText("Bold Text");
        boldButton.addActionListener(this);
        toolBar.add(boldButton);
        
        italicButton = new JButton(new ImageIcon("rtf_italic.gif"));
        italicButton.setToolTipText("Italic Text");
        italicButton.addActionListener(this);
        toolBar.add(italicButton);
        
        underlineButton = new JButton(new ImageIcon("rtf_underline.gif"));
        underlineButton.setToolTipText("Underline Text");
        underlineButton.addActionListener(this);
        toolBar.add(underlineButton);
        
        strikeThroughButton = new JButton(new ImageIcon("rtf_strikethrough.png"));
        strikeThroughButton.setToolTipText("Strike Through Text");
        strikeThroughButton.addActionListener(this);
        toolBar.add(strikeThroughButton);
        
        compileButton = new JButton(new ImageIcon("Compile.png"));
        compileButton.setToolTipText("Compile and Run Code");
        compileButton.addActionListener(this);
        toolBar.add(compileButton);
        
        frame.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getActionCommand().equalsIgnoreCase("New Tab"))
        {
            JTextPane textPane = new JTextPane();
            Font font = new Font("Serif", Font.PLAIN, 18);
            textPane.setFont(font);
            JScrollPane scrollPane = new JScrollPane(textPane);
            tabbedPane.addTab("Untitled " + tabCount++, scrollPane);
            
            UndoManager undoManager = new UndoManager();
            textPane.getDocument().addUndoableEditListener(undoManager);
            textPane.putClientProperty("UndoManager", undoManager);
            
            
        }
        else if(e.getActionCommand().equalsIgnoreCase("Open"))
        {
            // Showing file chooser dialog to get open file location
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
            fileChooser.setFileFilter(filter);
            int userSelection = fileChooser.showOpenDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) 
            {
                File fileToOpen = fileChooser.getSelectedFile();
                
                try 
                {
                    // Reading the contents of the selected file into a new tab
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToOpen));
                    JTextPane newPane = new JTextPane();
                    String line = bufferedReader.readLine();
                    while (line != null) 
                    {
                        newPane.getDocument().insertString(newPane.getDocument().getLength(), line + "\n", null);
                        line = bufferedReader.readLine();
                    }
                    JScrollPane newScrollPane = new JScrollPane(newPane);
                    newScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    tabbedPane.addTab(fileToOpen.getName(), newScrollPane);
                } 
                catch (IOException ex) 
                {
                    JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } 
                catch (BadLocationException ex) 
                {
                    JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if(e.getActionCommand().equalsIgnoreCase("Save"))
        {
            // Saving the contents of the currently selected tab
            int selectedIndex = tabbedPane.getSelectedIndex();
            JScrollPane selectedScrollPane = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
            JTextPane selectedPane = (JTextPane) selectedScrollPane.getViewport().getView();
            File selectedFile = (File) selectedScrollPane.getClientProperty("file");
            if (selectedFile != null) 
            {
                // Saving to an existing file
                try 
                {
                    FileWriter fileWriter = new FileWriter(selectedFile);
                    fileWriter.write(selectedPane.getText());
                    fileWriter.close();
                } 
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } 
            else 
            {
                // No file is associated with the current tab, so prompting the user for a location to save the file
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
                fileChooser.setFileFilter(filter);
                int userSelection = fileChooser.showSaveDialog(this);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) 
                {
                    File fileToSave = fileChooser.getSelectedFile();
                    
                    try 
                    {
                        FileWriter fileWriter = new FileWriter(fileToSave);
                        fileWriter.write(selectedPane.getText());
                        fileWriter.close();
                        selectedScrollPane.putClientProperty("file", fileToSave);
                        tabbedPane.setToolTipTextAt(selectedIndex, fileToSave.getAbsolutePath());
                        tabbedPane.setTitleAt(selectedIndex, fileToSave.getName());
                    } 
                    catch (IOException ex) 
                    {
                        JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
        else if(e.getActionCommand().equalsIgnoreCase("Save As"))
        {
            // Prompting the user for a location to save the file
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
            fileChooser.setFileFilter(filter);
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) 
            {
                File fileToSave = fileChooser.getSelectedFile();
                
                try 
                {
                    FileWriter fileWriter = new FileWriter(fileToSave);
                    fileWriter.write(selectedPane.getText());
                    fileWriter.close();
                    tabbedPane.setToolTipTextAt(selectedIndex, fileToSave.getAbsolutePath());
                    tabbedPane.setTitleAt(selectedIndex, fileToSave.getName());
                } 
                catch (IOException ex) 
                {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if(e.getActionCommand().equalsIgnoreCase("Close Tab"))
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex != -1) 
            {
                tabbedPane.removeTabAt(selectedIndex);
            }
        }
        else if(e.getActionCommand().equalsIgnoreCase("Exit"))
        {
            System.exit(0);
        }
        else if(e.getActionCommand().equalsIgnoreCase("Cut"))
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            selectedPane.cut();
        }
        else if(e.getActionCommand().equalsIgnoreCase("Copy"))
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            selectedPane.copy();
        }
        else if(e.getActionCommand().equalsIgnoreCase("Paste"))
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            selectedPane.paste();
        }
        else if(e.getActionCommand().equalsIgnoreCase("Select All"))
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            selectedPane.selectAll();
        }
        else if(e.getActionCommand().equalsIgnoreCase("Undo"))
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            UndoManager undoManager = ((UndoManager) selectedPane.getClientProperty("UndoManager"));
            if (undoManager.canUndo()) 
            {
                undoManager.undo();
            }
        }
        else if(e.getActionCommand().equalsIgnoreCase("Redo"))
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            UndoManager undoManager = ((UndoManager) selectedPane.getClientProperty("UndoManager"));
            if (undoManager.canRedo()) 
            {
                undoManager.redo();
            }
        }
        else if (e.getSource() == windowsThemeItem) 
        {
            setWindowsTheme();
        } 
        else if (e.getSource() == macThemeItem) 
        {
            setMacTheme();
        } 
        else if (e.getSource() == linuxThemeItem) 
        {
            setLinuxTheme();
        } 
        else if (e.getSource() == darkThemeItem) 
        {
            setDarkTheme();
        } 
        else if (e.getSource() == draculaThemeItem) 
        {
            setDraculaTheme();
        } 
        else if (e.getSource() == metalThemeItem) 
        {
            setMetalTheme();
        } 
        else if (e.getSource() == motifThemeItem) 
        {
            setMotifTheme();
        } 
        else if (e.getSource() == nimbusThemeItem) 
        {
            setNimbusTheme();
        }
        else if (e.getSource() == nimbusLightThemeItem) 
        {
            setNimbusLightTheme();
        }
        else if (e.getSource() == hackingThemeItem)
        {
            setHackingTheme();
        }
        else if (e.getSource() == contrastThemeItem) 
        {
            setContrastTheme();
        }
        else if (e.getSource() == fontBox) 
        {
            String fontName = (String)fontBox.getSelectedItem();
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            MutableAttributeSet attrs = selectedPane.getInputAttributes();
            StyleConstants.setFontFamily(attrs, fontName);
        } 
        else if (e.getSource() == sizeBox) 
        {
            String size = (String)sizeBox.getSelectedItem();
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            MutableAttributeSet attrs = selectedPane.getInputAttributes();
            StyleConstants.setFontSize(attrs, Integer.parseInt(size));
        } 
        else if (e.getSource() == styleBox) 
        {
            int style = styleBox.getSelectedIndex();
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            MutableAttributeSet attrs = selectedPane.getInputAttributes();
            StyleConstants.setBold(attrs, (style == 1 || style == 3));
            StyleConstants.setItalic(attrs, (style == 2 || style == 3));
        }
        else if (e.getSource() == themeBox) 
        {
            String themeName = (String)themeBox.getSelectedItem();
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            if (themeName.equals("Light")) 
            {
                selectedPane.setBackground(Color.WHITE);
                selectedPane.setForeground(Color.BLACK);
            } 
            else if (themeName.equals("Dark")) 
            {
                selectedPane.setBackground(Color.BLACK);
                selectedPane.setForeground(Color.WHITE);
            }
            else if (themeName.equals("Dracula")) 
            {
                Color c1 = new Color(40, 42, 54);
                Color c2 = new Color(255, 184, 108);
                selectedPane.setBackground(c1);
                selectedPane.setForeground(c2);
            }
            else if (themeName.equals("Dracula White")) 
            {
                Color c1 = new Color(40, 42, 54);
                Color c2 = new Color(248, 248, 242);
                selectedPane.setBackground(c1);
                selectedPane.setForeground(c2);
            }
            else if (themeName.equals("Geek")) 
            {
                Color c1 = new Color(0, 0, 0);
                Color c2 = new Color(0, 170, 0);
                selectedPane.setBackground(c1);
                selectedPane.setForeground(c2);
            }
        }
        else if (e.getSource() == boldButton) 
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            MutableAttributeSet attrs = selectedPane.getInputAttributes();
            StyleConstants.setBold(attrs, !StyleConstants.isBold(attrs));
        } 
        else if (e.getSource() == italicButton) 
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            MutableAttributeSet attrs = selectedPane.getInputAttributes();
            StyleConstants.setItalic(attrs, !StyleConstants.isItalic(attrs));
        }
        else if (e.getSource() == underlineButton) 
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            MutableAttributeSet attrs = selectedPane.getInputAttributes();
            StyleConstants.setUnderline(attrs, !StyleConstants.isUnderline(attrs));
        } 
        else if (e.getSource() == strikeThroughButton) 
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JTextPane selectedPane = (JTextPane) ((JScrollPane) tabbedPane.getComponentAt(selectedIndex)).getViewport().getView();
            MutableAttributeSet attrs = selectedPane.getInputAttributes();
            StyleConstants.setStrikeThrough(attrs, !StyleConstants.isStrikeThrough(attrs));
        }
        else if (e.getSource() == compileButton) 
        {
            int selectedIndex = tabbedPane.getSelectedIndex();
            JScrollPane selectedScrollPane = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
            JTextPane selectedPane = (JTextPane) selectedScrollPane.getViewport().getView();
            File fileToSave = (File) selectedScrollPane.getClientProperty("file");
            if (fileToSave != null) 
            {
                // Saving to an existing file
                try 
                {
                    FileWriter fileWriter = new FileWriter(fileToSave);
                    fileWriter.write(selectedPane.getText());
                    fileWriter.close();
                } 
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } 
            else 
            {
                // No file is associated with the current tab, so prompting the user for a location to save the file
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
                fileChooser.setFileFilter(filter);
                int userSelection = fileChooser.showSaveDialog(this);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) 
                {
                    fileToSave = fileChooser.getSelectedFile();
                    
                    try 
                    {
                        FileWriter fileWriter = new FileWriter(fileToSave);
                        fileWriter.write(selectedPane.getText());
                        fileWriter.close();
                        selectedScrollPane.putClientProperty("file", fileToSave);
                        tabbedPane.setToolTipTextAt(selectedIndex, fileToSave.getAbsolutePath());
                        tabbedPane.setTitleAt(selectedIndex, fileToSave.getName());
                    } 
                    catch (IOException ex) 
                    {
                        JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            try
            {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToSave));
                String line = bufferedReader.readLine();
                String str = "";
                while (line != null) 
                {
                    str+=line+"\n";
                    line = bufferedReader.readLine();
                }
                String filename = tabbedPane.getTitleAt(selectedIndex);
                final String s = str;
                try
                {
                    Thread thread = new Thread(() -> {
                        Client client = new Client("127.0.0.1", 5000, filename, s);
                    });
                    thread.start();
                    thread.join();
                }
                catch (InterruptedException exc)
                {
                    exc.printStackTrace();
                }
            }
            catch (IOException ex) 
            {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        
        textPane.requestFocus();
    }
    
    private void setWindowsTheme() 
    {
        try 
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Windows theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setMacTheme() 
    {
        try 
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Mac theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setLinuxTheme() 
    {
        try 
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Linux theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setDarkTheme() 
    {
        try 
        {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("control", new Color(128, 128, 128));
            UIManager.put("info", new Color(128, 128, 128));
            UIManager.put("nimbusBase", new Color(18, 30, 49));
            UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
            UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusGreen", new Color(176, 179, 50));
            UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
            UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
            UIManager.put("nimbusOrange", new Color(191, 98, 4));
            UIManager.put("nimbusRed", new Color(169, 46, 34));
            UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
            UIManager.put("text", new Color(230, 230, 230));
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Dark theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setDraculaTheme() 
    {
        try 
        {
            UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");
            UIManager.put("TextArea.background", new Color(45, 45, 45));
            UIManager.put("TextArea.foreground", new Color(187, 187, 187));
            UIManager.put("MenuBar.background", new Color(45, 45, 45));
            UIManager.put("MenuBar.foreground", new Color(187, 187, 187));
            UIManager.put("Menu.background", new Color(45, 45, 45));
            UIManager.put("Menu.foreground", new Color(187, 187, 187));
            UIManager.put("MenuItem.background", new Color(45, 45, 45));
            UIManager.put("MenuItem.foreground", new Color(187, 187, 187));
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Dracula theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setMetalTheme() 
    {
        try 
        {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Metal theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setMotifTheme() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Motif theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setNimbusTheme() 
    {
        try 
        {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Nimbus theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setNimbusLightTheme()
    {
        try 
        {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("control", new Color(214, 217, 223)); 
            UIManager.put("nimbusBase", new Color(200, 200, 200)); 
            UIManager.put("nimbusBorder", new Color(200, 200, 200));
            UIManager.put("nimbusFocus", new Color(140, 140, 140));
            UIManager.put("nimbusLightBackground", new Color(250, 250, 250));
            UIManager.put("nimbusSelectionBackground", new Color(180, 205, 255)); 
            UIManager.put("nimbusSelectedText", new Color(0, 0, 0)); 
            UIManager.put("text", new Color(0, 0, 0)); 
            UIManager.put("OptionPane.border", BorderFactory.createLineBorder(new Color(214, 217, 223), 5)); 
            UIManager.put("ProgressBar.border", BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); 
            UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); 
            UIManager.put("TextField.border", BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); 
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Nimbus theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setHackingTheme() 
    {
        try
        {
            Color bgColor = new Color(0, 0, 0);
            Color textColor = new Color(0, 255, 0);
            Color highlightColor = new Color(255, 0, 0);
            Color selectionColor = new Color(0, 0, 255);
            Color borderColor = new Color(0, 255, 0);
            
            Font textFont = new Font("Courier", Font.PLAIN, 12);
            Font buttonFont = new Font("Courier", Font.BOLD, 12);
            
            UIManager.put("control", bgColor);
            UIManager.put("text", textColor);
            UIManager.put("nimbusSelectionBackground", selectionColor);
            UIManager.put("nimbusSelectionForeground", textColor);
            
            UIManager.put("Button.select", highlightColor);
            UIManager.put("Button.highlight", highlightColor);
            UIManager.put("Button.shadow", highlightColor);
            UIManager.put("Button.darkShadow", highlightColor);
            UIManager.put("Button.background", bgColor);
            UIManager.put("Button.foreground", textColor);
            UIManager.put("Button.font", buttonFont);
            
            UIManager.put("TextField.background", bgColor);
            UIManager.put("TextField.foreground", textColor);
            UIManager.put("TextField.caretForeground", highlightColor);
            UIManager.put("TextField.selectionBackground", selectionColor);
            UIManager.put("TextField.selectionForeground", textColor);
            UIManager.put("TextField.inactiveBackground", bgColor);
            UIManager.put("TextField.inactiveForeground", textColor);
            
            UIManager.put("TextArea.background", bgColor);
            UIManager.put("TextArea.foreground", textColor);
            UIManager.put("TextArea.caretForeground", highlightColor);
            UIManager.put("TextArea.selectionBackground", selectionColor);
            UIManager.put("TextArea.selectionForeground", textColor);
            UIManager.put("TextArea.inactiveBackground", bgColor);
            UIManager.put("TextArea.inactiveForeground", textColor);
            
            UIManager.put("ScrollBar.background", bgColor);
            UIManager.put("ScrollBar.foreground", textColor);
            UIManager.put("ScrollBar.track", bgColor);
            UIManager.put("ScrollBar.thumb", textColor);
            UIManager.put("ScrollBar.thumbDarkShadow", highlightColor);
            UIManager.put("ScrollBar.thumbHighlight", highlightColor);
            UIManager.put("ScrollBar.thumbShadow", highlightColor);
            UIManager.put("ScrollBar.width", 10);
            
            UIManager.put("SplitPane.background", bgColor);
            UIManager.put("SplitPane.foreground", textColor);
            UIManager.put("SplitPaneDivider.background", borderColor);
            UIManager.put("SplitPaneDivider.foreground", borderColor);
            UIManager.put("SplitPaneDivider.draggingColor", borderColor);
            UIManager.put("SplitPane.border", BorderFactory.createEmptyBorder());
            
            UIManager.put("Table.background", bgColor);
            UIManager.put("Table.foreground", textColor);
            UIManager.put("Table.gridColor", borderColor);
            UIManager.put("Table.selectionBackground", selectionColor);
            UIManager.put("Table.selectionForeground", textColor);
            
            UIManager.put("TableHeader.background", bgColor);
            UIManager.put("TableHeader.foreground", textColor);
            UIManager.put("TableHeader.cellBorder", BorderFactory.createMatteBorder(0, 0, 2, 0, borderColor));
            UIManager.put("TableHeader.font", textFont);
            
            UIManager.put("Tree.background", bgColor);
            UIManager.put("Tree.foreground", textColor);
            UIManager.put("Tree.textForeground", textColor);
            UIManager.put("Tree.selectionForeground", textColor);
            UIManager.put("Tree.selectionBackground", selectionColor);
            UIManager.put("Tree.selectionBorderColor", selectionColor);
            UIManager.put("Tree.closedIcon", new ImageIcon(""));
            UIManager.put("Tree.openIcon", new ImageIcon(""));
            UIManager.put("Tree.leafIcon", new ImageIcon(""));
            SwingUtilities.updateComponentTreeUI(frame);
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Nimbus theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setContrastTheme()
    {
        try
        {
            Color backgroundColor = new Color(25, 25, 25);
            Color textColor = new Color(0, 255, 0);
            Color selectionColor = new Color(0, 128, 0);
            Color selectionTextColor = new Color(25, 25, 25);
            Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        
            UIDefaults defaults = UIManager.getLookAndFeelDefaults();
            defaults.put("Panel.background", backgroundColor);
            defaults.put("Button.background", backgroundColor);
            defaults.put("Button.foreground", textColor);
            defaults.put("Button.select", selectionColor);
            defaults.put("Button.selectForeground", selectionTextColor);
            defaults.put("TextField.background", backgroundColor);
            defaults.put("TextField.foreground", textColor);
            defaults.put("PasswordField.background", backgroundColor);
            defaults.put("PasswordField.foreground", textColor);
            defaults.put("TextArea.background", backgroundColor);
            defaults.put("TextArea.foreground", textColor);
            defaults.put("TextPane.background", backgroundColor);
            defaults.put("TextPane.foreground", textColor);
            defaults.put("EditorPane.background", backgroundColor);
            defaults.put("EditorPane.foreground", textColor);
            defaults.put("Menu.background", backgroundColor);
            defaults.put("Menu.foreground", textColor);
            defaults.put("MenuBar.background", backgroundColor);
            defaults.put("MenuItem.background", backgroundColor);
            defaults.put("MenuItem.foreground", textColor);
            defaults.put("OptionPane.background", backgroundColor);
            defaults.put("OptionPane.messageForeground", textColor);
            defaults.put("OptionPane.buttonBackground", backgroundColor);
            defaults.put("OptionPane.buttonForeground", textColor);
            defaults.put("OptionPane.border", BorderFactory.createLineBorder(textColor));
            defaults.put("OptionPane.messageFont", font);
            defaults.put("OptionPane.buttonFont", font);
            defaults.put("TabbedPane.background", backgroundColor);
            defaults.put("TabbedPane.foreground", textColor);
            defaults.put("TabbedPane.selected", selectionColor);
            defaults.put("TabbedPane.selectedForeground", selectionTextColor);
            defaults.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
            defaults.put("TabbedPane.tabInsets", new Insets(0, 0, 0, 0));
            defaults.put("TabbedPane.borderHightlightColor", selectionColor);
            defaults.put("TabbedPane.focus", selectionColor);
            defaults.put("TabbedPane.focusColor", selectionColor);
            defaults.put("ScrollBar.thumb", textColor);
            defaults.put("ScrollBar.thumbDarkShadow", textColor);
            defaults.put("ScrollBar.thumbHighlight", textColor);
            defaults.put("ScrollBar.thumbShadow", textColor);
            defaults.put("ScrollBar.track", backgroundColor);
            defaults.put("ScrollBar.trackHighlight", selectionColor);
            defaults.put("ScrollBar.width", 10);
            defaults.put("ScrollPane.border", BorderFactory.createLineBorder(textColor));
            defaults.put("Viewport.background", backgroundColor);
            defaults.put("Viewport.foreground", textColor);
            SwingUtilities.updateComponentTreeUI(frame);
        }
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error setting Nimbus theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) 
    {
        new TextEditor();
    }
}
