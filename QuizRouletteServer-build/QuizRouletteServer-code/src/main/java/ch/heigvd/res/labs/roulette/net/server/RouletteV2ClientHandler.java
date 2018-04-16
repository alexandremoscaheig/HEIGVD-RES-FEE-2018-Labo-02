package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import ch.heigvd.res.labs.roulette.data.StudentsList;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 * @author Aebischer Lenny
 * @author Mosca Alexandre
 */
public class RouletteV2ClientHandler implements IClientHandler {

    final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
    private final IStudentsStore store;

    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store = store;
    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

        writer.println("Hello. Online HELP is available. Will you find it?");
        writer.flush();

        String command;
        int nbCommands = 0;
        boolean done = false;

        while (!done && ((command = reader.readLine()) != null)) {
            LOG.log(Level.INFO, "COMMAND: {0}", command);
            nbCommands++;
            switch (command.toUpperCase()) {
                case RouletteV2Protocol.CMD_RANDOM:
                    RandomCommandResponse cmdResponse = new RandomCommandResponse();
                    try {
                        cmdResponse.setFullname(store.pickRandomStudent().getFullname());
                    } catch (EmptyStoreException ex) {
                        cmdResponse.setError("There is no student, you cannot pick a random one");
                    }
                    writer.println(JsonObjectMapper.toJson(cmdResponse));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_HELP:
                    writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;

                case RouletteV2Protocol.CMD_INFO:
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_LOAD:
                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();

                    int nbStudentsBefore = store.getNumberOfStudents();
                    String status;

                    try {
                        store.importData(reader);
                        status = RouletteV2Protocol.SUCCESS;
                    } catch (IOException ex){
                        status = RouletteV2Protocol.FAILURE;
                    }
                    int nbStudentsAfter = store.getNumberOfStudents();
                    writer.println(JsonObjectMapper.toJson(new LoadCommandResponse(status, nbStudentsAfter - nbStudentsBefore)));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_LIST:
                    StudentsList list = new StudentsList();
                    list.setStudents(store.listStudents());
                    writer.println(JsonObjectMapper.toJson(list));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_BYE:
                    done = true;
                    writer.println(JsonObjectMapper.toJson(new ByeCommandResponse(RouletteV2Protocol.SUCCESS, nbCommands)));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_CLEAR:
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;

                default:
                    writer.println("Huh? please use HELP if you don't know what commands are available.");
                    writer.flush();
                    break;
            }
            writer.flush();
        }

    }

}