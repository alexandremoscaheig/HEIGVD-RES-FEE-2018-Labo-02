package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {


    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private InfoCommandResponse info;


    @Override
    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        readBuffer();
    }

    @Override
    public void disconnect() throws IOException {
        socket.close();
        socket = null;
        input.close();
        input = null;
        output.close();
        output = null;
        info = null;
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        if(!isConnected()) throw new IOException();
        this.writeCmd(RouletteV1Protocol.CMD_LOAD);
        System.out.println(this.readBuffer());
        this.writeCmd(fullname);
        this.writeCmd(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        System.out.println(this.readBuffer());
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if(!isConnected()) throw new IOException();
        this.writeCmd(RouletteV1Protocol.CMD_LOAD);
        System.out.println(this.readBuffer());
        for(Student s : students)
            this.writeCmd(s.getFullname());
        this.writeCmd(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        System.out.println(this.readBuffer());

    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if(!isConnected()) throw new IOException();
        this.writeCmd(RouletteV1Protocol.CMD_RANDOM);

        RandomCommandResponse rcr = JsonObjectMapper.parseJson(this.readBuffer(), RandomCommandResponse.class);
        if(!rcr.getError().isEmpty())
            throw new EmptyStoreException();
        return new Student(rcr.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        if(!isConnected()) throw new IOException();
        this.refreshInfos();
        return this.info.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        if(!isConnected()) throw new IOException();
        this.refreshInfos();
        return this.info.getProtocolVersion();
    }

    private void writeCmd(String cmd) throws IOException{
        this.output.write(cmd + "\n");
        this.output.flush();
    }

    private void refreshInfos() throws IOException{
        this.writeCmd(RouletteV1Protocol.CMD_INFO);
        String response = this.readBuffer();
        this.info = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
    }


    private String readBuffer() throws IOException{

        return this.input.readLine();
    }

}
