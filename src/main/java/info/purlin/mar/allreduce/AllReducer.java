package info.purlin.mar.allreduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import info.purlin.mar.reducible.AllReducible;

/**
 * Allreduce operator
 *
 * @author Erheng Zhong (purlin.zhong@gmail.com)
 */
public class AllReducer {
    /**
     * IP address of spanning tree server
     */
    private String masterIP = "localhost";
    /**
     * Port of spanning tree
     */
    private int masterPort = 10090;
    /**
     * Port for data communication
     */
    private int clientPort = 10091;
    /**
     * TCP delay seconds
     */
    protected int linger = 100;
    /**
     * TCP buffer size
     */
    protected int buff = 8192 * 2;
    /**
     * Socket Timeout
     */
    protected int timeout = 200;
    /**
     * IP Addresses of parent, left child and right child
     */
    private String[] neighborIP = {null, null, null};
    /**
     * Ports of parent, left child and right child
     */
    private int[] neighborPort = {-1, -1, -1};
    /**
     * Server socket for the current client
     */
    protected ServerSocket ss = null;
    /**
     * The rank of the current client
     */
    public int rank;

    /**
     * Constructor
     *
     * @param masterIP:   IP address of spanning tree server
     * @param masterPort: Port of spanning tree server
     */
    public AllReducer(String masterIP, int masterPort) {
        this.masterIP = masterIP;
        this.masterPort = masterPort;
    }

    /**
     * Split a data line to [head \t IP]
     *
     * @param line: a data line
     */
    private void splitTreeLine(String line) {
        String[] items = line.split("\t");
        if (items[0].equals("#C")) {
            rank = Integer.parseInt(items[1]);
            clientPort = Integer.parseInt(items[2]);
        }
        if (items[0].equals("#P")) {
            neighborIP[0] = items[1];
            neighborPort[0] = Integer.parseInt(items[2]);
        }
        if (items[0].equals("#L")) {
            neighborIP[1] = items[1];
            neighborPort[1] = Integer.parseInt(items[2]);
        }
        if (items[0].equals("#R")) {
            neighborIP[2] = items[1];
            neighborPort[2] = Integer.parseInt(items[2]);
        }
    }

    /**
     * Set socket parameter
     *
     * @param socket
     * @throws SocketException
     */
    private void setSocket(Socket socket) throws SocketException {
        socket.setSoLinger(true, linger);
        socket.setSendBufferSize(buff);
        socket.setReceiveBufferSize(buff);
        socket.setKeepAlive(false);
    }

    /**
     * Initialize, obtain tree structure
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void init() throws IOException {
        // Reset variables
        for (int i = 0; i < 3; i++) neighborIP[i] = null;
        Socket socket = null;
        try {
            socket = new Socket(masterIP, masterPort);
            setSocket(socket);
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //Receive message from spanning tree server
            while (!"#Start".equals(is.readLine())) ;
            for (int i = 0; i < 4; i++) splitTreeLine(is.readLine());
            os.println("#End");
            os.flush();
            socket.close();
            ss = new ServerSocket(clientPort, 3);
            Thread.sleep(timeout);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    /**
     * Allreduce main function
     *
     * @param dataObj, data object
     * @return updated data object
     * @throws IOException
     */
    public AllReducible run(AllReducible dataObj) throws IOException {

        AllReducible newDataObj = dataObj;
        //reduce data from children
        try {
            for (int i = 1; i <= 2; i++) {
                if (!neighborIP[i].equals("None")) {
                    Socket socket = ss.accept();
                    setSocket(socket);
                    ObjectInputStream is = new ObjectInputStream(new GZIPInputStream(socket.getInputStream()));
                    newDataObj.sum((AllReducible) is.readObject());
                    is.close();
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //reduce results to parent
        if (!neighborIP[0].equals("None")) {
            try {
                Socket socket = new Socket();
                SocketAddress sa = new InetSocketAddress(neighborIP[0], neighborPort[0]);
                socket.connect(sa, timeout);
                setSocket(socket);
                ObjectOutputStream os = new ObjectOutputStream(new GZIPOutputStream(socket.getOutputStream()));
                os.writeObject(newDataObj);
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //obtain updated value from parent
            try {
                Socket socket = ss.accept();
                ObjectInputStream is = new ObjectInputStream(new GZIPInputStream(socket.getInputStream()));
                newDataObj = (AllReducible) is.readObject();
                is.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        //broadcast to children
        try {
            for (int i = 1; i <= 2; i++) {
                if (!neighborIP[i].equals("None")) {
                    Socket socket = new Socket();
                    SocketAddress sa = new InetSocketAddress(neighborIP[i], neighborPort[i]);
                    socket.connect(sa, timeout);
                    setSocket(socket);
                    ObjectOutputStream os = new ObjectOutputStream(new GZIPOutputStream(socket.getOutputStream()));
                    os.writeObject(newDataObj);
                    os.close();
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(newDataObj.toString());
        return newDataObj;
    }

    public void clean() throws IOException {
        if (ss != null && !ss.isClosed()) ss.close();
    }

}
