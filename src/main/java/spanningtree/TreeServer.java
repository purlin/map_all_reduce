package spanningtree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Server for spanning tree
 *
 * @author Erheng Zhong (purlin.zhong@gmail.com)
 */
public class TreeServer {
    /**
     * Number of clients
     */
    static protected int numClients = 9999;
    /**
     * Port
     */
    static protected int port = 10090;
    /**
     * TCP delay seconds
     */
    static protected int linger = 100;
    /**
     * TCP buffer size
     */
    static protected int buff = 8192 * 2;

    /**
     * Start server
     *
     * @throws IOException
     */
    public static void start() throws IOException {
        ServerSocket ss = null;
        try {
            // create server socket
            ss = new ServerSocket(port, numClients + 1);
            System.out.println("Server Socket Running...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SpanningTree spanningTree = new SpanningTree();
        int current_id = 0;
        try {
            while (true) {
                // create a socket to communicate with various clients
                Socket currentSocket = ss.accept();
                currentSocket.setSoLinger(true, linger);
                currentSocket.setSendBufferSize(buff);
                currentSocket.setReceiveBufferSize(buff);
                //System.out.println("Socket Listening...");
                // build a tree node
                TreeNode currentNode = new TreeNode(currentSocket);
                currentNode.port = port + current_id + 1;
                spanningTree.Tree.put(current_id, currentNode);
                current_id += 1;
                System.out.println("New Node " + current_id + " " + currentSocket.getInetAddress().toString() + " " + currentSocket.getPort());
                // time to create spanning tree
                if (current_id == numClients) {
                    System.out.println("Tree Building...");
                    // build tree
                    spanningTree.buildTree(0, -1);
                    //System.out.println("Message Sending... #"+numClients);
                    // send tree structure to clients
                    for (Integer id : spanningTree.Tree.keySet()) {
                        TreeNode node = spanningTree.Tree.get(id);
                        PrintWriter os = new PrintWriter(node.socket.getOutputStream());
                        BufferedReader is = new BufferedReader(new InputStreamReader(node.socket.getInputStream()));
                        //System.out.println("Node "+node.ip);
                        // start signal
                        os.println("#Start");
                        // current, parent, left child, right child
                        os.println("#C\t" + id + "\t" + node.port);
                        if (node.parent_id != -1)
                            os.println("#P\t" + spanningTree.Tree.get(node.parent_id).ip + "\t" + spanningTree.Tree.get(node.parent_id).port);
                        else os.println("#P\tNone\t-1");
                        if (node.left_id != -1)
                            os.println("#L\t" + spanningTree.Tree.get(node.left_id).ip + "\t" + spanningTree.Tree.get(node.left_id).port);
                        else os.println("#L\tNone\t-1");
                        if (node.right_id != -1)
                            os.println("#R\t" + spanningTree.Tree.get(node.right_id).ip + "\t" + spanningTree.Tree.get(node.right_id).port);
                        else os.println("#R\tNone\t-1");
                        os.flush();
                        // wait ack
                        is.readLine();
                        //System.out.println (is.readLine()) ;
                        // close stream
                        os.close();
                        is.close();
                    }
                    System.out.println("Tree Built!");
                    // clean
                    for (TreeNode node : spanningTree.Tree.values()) node.socket.close();
                    spanningTree.Tree.clear();
                    current_id = 0;
                    // ss.close(); ss = new ServerSocket(port, numClients+1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ss.close();
        }
    }

    /**
     * @param args
     * @throws ParseException
     * @throws IOException
     */
    public static void main(String[] args) throws ParseException, IOException {
        // Create a Parser
        CommandLineParser parser = new BasicParser();
        Options options = new Options();
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("n", "nodes", true, "Number of Client Nodes");
        options.addOption("p", "port", true, "Port number");
        HelpFormatter formatter = new HelpFormatter();
        // Parse the program arguments
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("n")) numClients = Integer.parseInt(cmd.getOptionValue("n"));
        else {
            formatter.printHelp("ant", options);
            return;
        }
        if (cmd.hasOption("p")) port = Integer.parseInt(cmd.getOptionValue("p"));
        else {
            formatter.printHelp("ant", options);
            return;
        }
        // Main process
        start();
    }

}
