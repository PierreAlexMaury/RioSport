package com.google.riosport.webservice;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import com.google.riosport.elements.*;
/**
 * Created by Rémi Prévost on 30/10/2014.
 */
public final class WebServiceInterface {

    public static boolean __DEBUG__ = true;
    private static final String NAMESPACE = "https://www.etud.insa-toulouse.fr/~pamaury/webService/";
    private static final String MAIN_REQUEST_URL = "https://www.etud.insa-toulouse.fr/~pamaury/webService/server.php?asmx";

    private ArrayList<Event> list_event = new ArrayList<Event>();
    private Event[] array_event;

    public WebServiceInterface() {}

    @SuppressWarnings("unused")
    public static int addEvent(Event event) throws WebServiceException{
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#AddEvent";
        String METHODNAME = "AddEvent";

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","addEvent launched");

        SoapObject request = new SoapObject(NAMESPACE,METHODNAME);
        request.addProperty("sport", event.getSport());
        request.addProperty("owner", event.getOwner());
        request.addProperty("visibility",event.getVisibility());
        request.addProperty("date_time",event.getDateTime());
        request.addProperty("duration",event.getDuration());
        request.addProperty("location",event.getLocation());
        request.addProperty("longitude",event.getLongitude());
        request.addProperty("latitude",event.getLatitude());
        request.addProperty("description",event.getDescription());

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","addEvent finished");

        return (Integer)getResult(request,SOAP_ACTION);
    }

    @SuppressWarnings("unused")
    public ArrayList<Event> getAvailableEvents(int visibility, ArrayList<String> array_sports, int id_user) throws WebServiceException {
        ArrayList<EventThread> list_thread = new ArrayList<EventThread>();

        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#GetEventsId";
        String METHODNAME = "GetEventsId";

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getAvailableEvents launched");

        SoapObject request = new SoapObject(NAMESPACE, METHODNAME);
        if (visibility == Event.PUBLIC)
            request.addProperty("visibility", "public");
        else if (visibility == Event.PRIVATE)
            request.addProperty("visibility", "private");
        else
            throw new WebServiceException("Invalid visibility code sent");

        String sports_string = "";
        for (String sport : array_sports)
            sports_string += sport+'$';

        if (!array_sports.isEmpty())
            sports_string = sports_string.substring(0,sports_string.length() -1);

        request.addProperty("sports_string", sports_string);
        request.addProperty("id_user", id_user);

        Vector result = (Vector)getResult(request,SOAP_ACTION);


        list_event.clear();
        array_event = new Event[result.size()];

        for (int i = 0; i < result.size(); i++) {
            int event_id = Integer.parseInt((String) result.get(i));
            EventThread event_thread = new EventThread("GetEventThread"+event_id,event_id,i);
            list_thread.add(event_thread);
            event_thread.start();
        }

        boolean finished = false;
        while (!finished) {
            int i = 0;
            finished = true;
            while (finished && i < list_thread.size()) {
                if (!(list_thread.get(i).getState() == Thread.State.TERMINATED)) {
                    finished = false;
                }
                i++;
            }
        }

        for (Event e: array_event)
            list_event.add(e);

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getAvailableEvents finished");

        return this.list_event;
    }

    @SuppressWarnings("unused")
    public static ArrayList<User> getInvolvedUsers(int id_event) throws WebServiceException {
        ArrayList<User> list_users = new ArrayList<User>();
        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getInvolcedUsers launched");
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService/GetInvolvedIn";
        String METHODNAME = "GetInvolvedIn";
        SoapObject request = new SoapObject(NAMESPACE, METHODNAME);
        request.addProperty("id_event", id_event);
        String result;
        result = (String) getResult(request, SOAP_ACTION);
        String[] input = result.split("x@x@x");
        int flag = 0;
        User user = new User();
        for (String str: input) {
            if (flag == 0 && !str.isEmpty()) {
                user = new User();
                user.setId_user(Integer.parseInt(str));
                flag = 1;
            }
            else if (flag == 1) {
                user.setFull_name(str);
                flag = 2;
            }
            else {
                user.setUrl_avatar(str);
                flag = 0;
                list_users.add(user);
            }
        }

        Log.e("$$$$$$$$$$$$$$",list_users.toString());
        return list_users;
    }

    @SuppressWarnings("unused")
    public static int addUser(String external_id, String log_manager, User user) throws WebServiceException{
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#AddUser";
        String METHODNAME = "AddUser";

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","addUser launched");

        SoapObject request = new SoapObject(NAMESPACE,METHODNAME);
        request.addProperty("external_id",external_id);
        request.addProperty("log_manager", log_manager);
        request.addProperty("full_name",user.getFull_name());
        request.addProperty("url_avatar",user.getUrl_avatar());
        request.addProperty("gender",user.getGender().toString());
        request.addProperty("date_of_birth",user.getDate_of_birth());

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","addUser finished");

        return (Integer)getResult(request,SOAP_ACTION);
    }

    @SuppressWarnings("unused")
    public static User getUser(int id_user) throws WebServiceException{
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#GetUser";
        String METHODNAME = "GetUser";

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getUser launched");

        SoapObject request = new SoapObject(NAMESPACE, METHODNAME);
        request.addProperty("id_user", id_user);


        SoapObject result = (SoapObject)getResult(request,SOAP_ACTION);
        User user = new User();
        try {
            user.setId_user((Integer) result.getProperty("id_user"));
            user.setFull_name((String) result.getProperty("full_name"));
            user.setUrl_avatar((String) result.getProperty("url_avatar"));
            String gender = (String) result.getProperty("gender");
            if (gender.equals(Gender.male.toString()))
                user.setGender(Gender.male);
            else if (gender.equals(Gender.female.toString()))
                user.setGender(Gender.female);
            else
                user.setGender(Gender.undefined);
            user.setDate_of_birth((String) result.getProperty("date_of_birth"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","addUser finished");

        return user;
    }

    @SuppressWarnings("unused")
    public static int isUser(String log_manager, String external_id) throws WebServiceException{
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#IsUser";
        String METHODNAME = "IsUser";

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","isUser launched");

        SoapObject request = new SoapObject(NAMESPACE, METHODNAME);
        request.addProperty("log_manager", log_manager);
        request.addProperty("external_id", external_id);

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","isUser finished");

        return (Integer)getResult(request,SOAP_ACTION);
    }

    @SuppressWarnings("unused")
    public static ArrayList<User> getFriendsOf(int id_user) throws WebServiceException{
        ArrayList<User> friends_list = new ArrayList<User>();
        ArrayList<Integer> id_friends_list = getIdFriendsOf(id_user);

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getFriendsOf launched");

        for (int id : id_friends_list)
            friends_list.add(getUser(id));

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getFriendsOf finished");

        return friends_list;
    }

    @SuppressWarnings("unused")
    public static ArrayList<Practiced> getPracticedSports(int id_user) throws WebServiceException{
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#GetPracticedSports";
        String METHODNAME = "GetPracticedSports";
        ArrayList<Practiced> practiced_list = new ArrayList<Practiced>();

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getPracticedSports launched");

        SoapObject request = new SoapObject(NAMESPACE, METHODNAME);
        request.addProperty("id_user", id_user);

        Vector result = (Vector)getResult(request,SOAP_ACTION);

        for (int i = 0; i < result.size(); i = i + 2) {
            practiced_list.add(new Practiced((String)result.get(i),(String)result.get(i+1)));
        }

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getPracticedSports finished");

        return practiced_list;
    }

    @SuppressWarnings("unused")
    public static int getInternalId(String log_manager, String external_id) throws WebServiceException{
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#GetInternalId";
        String METHODNAME = "GetInternalId";

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getInternalId launched");

        SoapObject request = new SoapObject(NAMESPACE, METHODNAME);
        request.addProperty("log_manager", log_manager);
        request.addProperty("external_id", external_id);

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getInternalId finished");

        return (Integer)getResult(request,SOAP_ACTION);
    }

    @SuppressWarnings("unused")
    public static ArrayList<String> getSports() throws WebServiceException {
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#GetSports";
        String METHODNAME = "GetSports";
        ArrayList<String> sports_list = new ArrayList<String>();

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getSports launched");

        SoapObject request = new SoapObject(NAMESPACE, METHODNAME);

        Vector result = (Vector)getResult(request,SOAP_ACTION);

        for (int i = 0; i < result.size(); i++) {
            sports_list.add((String)result.get(i));
        }

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getSports finished");

        return sports_list;
    }

    @SuppressWarnings("unused")
    public static ArrayList<String> getLevels() throws WebServiceException {
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#GetLevels";
        String METHODNAME = "GetLevels";
        ArrayList<String> levels_list = new ArrayList<String>();

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getLevels launched");

        SoapObject request = new SoapObject(NAMESPACE, METHODNAME);

        Vector result = (Vector)getResult(request,SOAP_ACTION);

        for (int i = 0; i < result.size(); i++) {
            levels_list.add((String) result.get(i));
        }

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getLevels finished");

        return levels_list;
    }

/***********************************************************************************************/
/********************************* PRIVATE METHODS *********************************************/
/***********************************************************************************************/

    private static SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    private static HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(MAIN_REQUEST_URL);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\\\"1.0\\\" encoding= \\\"UTF-8\\\" ?-->");

        return ht;
    }

    private static Object getResult(SoapObject request, String SOAP_ACTION) throws WebServiceException{
        try {
            SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
            HttpTransportSE ht = getHttpTransportSE();

            MarshalDouble md = new MarshalDouble();
            md.register(envelope);

            ht.call(SOAP_ACTION, envelope);

            return  envelope.getResponse();
        } catch (java.net.SocketTimeoutException e) {
            if (!__DEBUG__)
                throw new WebServiceException("Failed to connect to web service");
            else {
                e.printStackTrace();
                return null;
            }
        }catch (java.net.UnknownHostException e) {
            if (!__DEBUG__)
                throw new WebServiceException("Failed to connect to web service");
            else {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            if (!__DEBUG__)
                throw new WebServiceException("Failed to get result from Web Service for SOAP_ACTION : "+SOAP_ACTION);
            else {
                e.printStackTrace();
                return null;
            }
        }
    }


    private static ArrayList<Integer> getIdFriendsOf(int id_user) throws WebServiceException {
        String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#GetFriendsOf";
        String METHODNAME = "GetFriendsOf";
        ArrayList<Integer> friends_id_list = new ArrayList<Integer>();

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getIdFriendsOf launched");

        SoapObject request = new SoapObject(NAMESPACE, METHODNAME);
        request.addProperty("id_user", id_user);

        Vector result = (Vector)getResult(request,SOAP_ACTION);

        for (int i = 0; i < result.size(); i++) {
            friends_id_list.add(Integer.parseInt((String)result.get(i)));
        }

        if (__DEBUG__)
            Log.d("DEBUG-WEBSERVICE","getIdFriendsOf finished");

        return friends_id_list;
    }

    private static class MarshalDouble implements Marshal{
        public Object readInstance(XmlPullParser parser, String namespace, String name,PropertyInfo expected) throws IOException, XmlPullParserException {
            return Double.parseDouble(parser.nextText());
        }

        public void register(SoapSerializationEnvelope cm) {
            cm.addMapping(cm.xsd, "double", Double.class, this);
        }

        public void writeInstance(XmlSerializer writer, Object obj) throws IOException {
            writer.text(obj.toString());
        }
    }


    private class EventThread extends Thread {
        private int id_event;
        private int array_location;

        public EventThread(String name, int id_event, int array_location) {
            super(name);
            this.id_event = id_event;
            this.array_location = array_location;

            if (__DEBUG__)
                Log.d("DEBUG-WEBSERVICE","THREAD "+this.getName()+" STATE : "+"CREATED");
        }

        public void run() {
            Log.d("DEBUG-WEBSERVICE","THREAD "+this.getName()+" STATE : "+"STARTED");

            String SOAP_ACTION = "https://www.etud.insa-toulouse.fr/~pamaury/webService#GetEvent";
            String METHODNAME = "GetEvent";

            SoapObject request = new SoapObject(NAMESPACE, METHODNAME);
            request.addProperty("id_event", this.id_event);

            String result;
            try {
                result = (String)getResult(request,SOAP_ACTION);
                Event event = new Event();
                String[] input = result.split("##b#a###");
                event.setId_event(Integer.parseInt(input[0]));
                event.setSport(input[1]);
                event.setOwner(Integer.parseInt(input[2]));
                if (input[3].equals("public"))
                    event.setVisibility(Event.PUBLIC);
                else if (input[3].equals("private"))
                    event.setVisibility(Event.PRIVATE);
                else
                    event.setVisibility(Event.PRIVATE);
                event.setDate_time(input[4]);
                event.setDurationMinute(Integer.parseInt(input[5]));
                event.setLocation(input[6]);
                event.setLongitude(Double.parseDouble(input[7]));
                event.setLatitude(Double.parseDouble(input[8]));
                if (input.length == 10)
                    event.setDescription(input[9]);

                safeThreadAddEvent(event, array_location);
                if (__DEBUG__)
                    Log.d("DEBUG-WEBSERVICE","THREAD "+this.getName()+" STATE : "+"FINISHED");
            } catch (WebServiceException e) {
                e.printStackTrace();
            }
        }

        private synchronized void safeThreadAddEvent(Event event, int array_location) {
            if (__DEBUG__)
                Log.d("DEBUG-WEBSERVICE","WRITE IN array_event");
            array_event[array_location] = event;
        }
    }
}