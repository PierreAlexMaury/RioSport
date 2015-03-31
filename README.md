# RioSport

Authors : Anthony Laloy, Pierre-Alexandre Maury, Remi Prevost
Under the supervision of: Sergio Barbosa Villas-Boas (sbVB), www.sbvb.com.br,
sbvillasboas@gmail.com, instructor at UFRJ, Brazil

Classes of the RioSport application

Package: com.google.riosport/elements
				
public class Event {} : Defines an event with its differents properties.
		
public class Practised {} : Gives the level of a practised sport only
		
public class User {} : Defines an user with its differents attributes


Package: com.google.riosport
		
public class CreateEvent extends FragmentActivity {} : Android class used 
to create an event via different choices which the user has to make. it 
extends a fragment activity because a map fragment is used in this class.

public class CustomAutoComplete extends AutoCompleteTextView {} : This 
class allows the user to use spaces (“ “)  in an AutoCompleteTextView 
to find a place for his event because at first “spaces” disabled the 
functionality of the google map to propose a list of places.

public class customTextView extends TextView {} : This class introduces 
the “JeNeTOublieraiJamais.ttf” police for the writing in a TextView.

public class FeedItem {} : This class defines an item of the event feed 
(a location, a date, a picture of the sport a description).

public class FeedListAdapter extends BaseAdapter {} : This links each
 item to the corresponding layout.

public class MyFragment extends FragmentActivity implements ActionBar.TabListener{} : 
This class implements 3 fragments which correspond to a special functionality
 and it implements an ActionBar.TabListener in order to navigate between 
the three different fragments.

public class LeftFragmentPage extends Fragment {} : This class is the left
 fragment of the MyFragment class. In this class a list of friends is shown and the 
user can add friends.

public class RightFragmentPage extends Fragment {} : This class is the
 map fragment part of the map. it allows the user to show the different event 
which exist according to the situation of the event (public, private) and the sport 
(all sports or those practised by the user).

public class MiddleFragmentPage extends Fragment {} : This class links
 FeedItem {} class and FeedListAdapter class in order to create a feed of event.

public class ListCustom extends ArrayAdapter<String> {} : This class 
links a picture to corresponding sport in order to fill a list visible 
in the CreateEvent class.

public class ListCustomFriend extends ArrayAdapter<String> {} : This class 
links a profile picture to the corresponding name of the person in order 
to fill a list too which is visible in the LeftFragment class.

public class Main extends Activity implements View.OnClickListener,
GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {} :
 This class is the first one which the user sees. it allows the connexion to 
a google account and therefore the loading of the user informations.

public class PlaceJsonParser {} : It is used to read a Json file and recover
 informations for a place in google map (name, id, reference and description).

public class PlaceDetailsJsonParser {} :  It is used to read a Json file
 and recover the location for a place in google map (latitude and longitude).

public class MyPagerAdapter extends FragmentPagerAdapter {} :  It is used
 to order the different fragment of the MyFragment class.

public class ParticipantAdapter extends BaseAdapter {} : This class links
 a profile picture to the corresponding name of the person in order to fill
 a list of participants of an event which is visible in the Print_Event class.

public class Print_Event extends Activity {} : This class is used to show
 a full event (+ the list of the participant)


Package: com.google.riosport/webservice

public class WebServiceInterface {} : This class is an interface that deals
with the exchanges of datas between the java application and the web service.
Its gives a set of functions to get, add and update informations to the web
service.

public class WebServiceException extends Exception {} : Class used to raise
exeptions from the WebServiceInterface class.
