package app.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import app.database.Database;
import app.database.DatabaseImplementation;
import app.database.RepositoryImplementation;
import app.database.DBConfig;
import app.database.DBConfigImplementation;
import app.Constants;
import app.models.entity.Entity;
import app.models.root.RootNode;

public class MainFrame extends JFrame {

	private static MainFrame instance;
	
    private Database database;
    private JTree jTree;
    private RootNode root;

	private MainFrame() {
        DBConfig dbConfig = initSettings();
        this.database = new DatabaseImplementation(new RepositoryImplementation(dbConfig));
        this.root = database.initRoot();

		initViews();
	}

	private void initViews() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();

        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize((int) (screenWidth / 1.25), (int) (screenHeight / 1.25));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Baza podataka TIM 20");
        this.setLocationRelativeTo(null);

        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        jTree = new JTree();
        jTree.setModel(treeModel);
        jTree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Object selection = jTree.getLastSelectedPathComponent();
                if (!(selection instanceof Entity))
                    return;

                root.notifyEntitySelected((Entity) selection);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        JScrollPane scrollTree = new JScrollPane(jTree);
        scrollTree.setMinimumSize(new Dimension(200, 150));
        
        EntitiesView primeTable = new EntitiesView(root);
        EntitiesHelperView secTable = new EntitiesHelperView(root);

        JSplitPane splitTables = new JSplitPane(JSplitPane.VERTICAL_SPLIT, primeTable, secTable);
        splitTables.setResizeWeight(0.5);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTree, splitTables);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.1);
        
        getContentPane().add(splitPane, BorderLayout.CENTER);
        setVisible(true);
	}
	
	private DBConfig initSettings() {
        DBConfig settingsImplementation = new DBConfigImplementation();
        settingsImplementation.addParameter("mssql_ip", Constants.MSSQL_IP);
        settingsImplementation.addParameter("mssql_database", Constants.MSSQL_DATABASE);
        settingsImplementation.addParameter("mssql_username", Constants.MSSQL_USERNAME);
        settingsImplementation.addParameter("mssql_password", Constants.MSSQL_PASSWORD);
        return settingsImplementation;
    }

    public Database getDatabase() {
        return database;
    }

    public static MainFrame getInstance() {
	    if (instance == null)
	        instance = new MainFrame();

	    return instance;
    }

    public RootNode getRoot() {
        return root;
    }
}
