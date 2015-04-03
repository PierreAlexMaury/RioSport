<?php

/*
 * Name: server.php
 * Created by Rémi Prévost on 30/10/2014.
 * for RioSport web service provider
*/

/************************************************************************************/
/********************************* PUBLIC FUNCTIONS *********************************/
/************************************************************************************/

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@0@@*/
/*@@@@@@@@@@@@@@@@@@ EVENTS @@@@@@@@@@@@@@@@@@*/


/*
 * Description: Add a new event in the data base and declare a new involved user. Returns 1 if success, -1 in case of error.
 * Input: sport:string, owner:int, visibility:string, date_time:string, duration:int, longitude:float, latitude:float, description:string
 * Output: success:int
*/
function AddEvent ($sport, $id_user, $visibility, $date_time, $duration, $location, $longitude, $latitude, $description) {
	if (!($db = DataBaseConnection()))
		return -1;
		
	$new_event = $db->prepare('
		INSERT INTO Events (
			sport, 
			owner, 
			visibility, 
			date_time, 
			duration, 
			location,
			longitude, 
			latitude, 
			description) 
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
	');
		
	if (!$new_event->execute(array($sport, $id_user, $visibility, $date_time, $duration, $location, $longitude, $latitude, $description)))
		return -1;
		
	$id_event = $db->prepare('
		SELECT id AS id_event FROM Events
		WHERE sport = ?
		AND owner = ?
		AND date_time = ?
	');
	
	if (!$id_event->execute(array($sport, $id_user, $date_time)))
		return -1;
	
	$id_event = $id_event->fetch();
	$id_event = $id_event['id_event'];
		
	$new_envolved = $db->prepare('
		INSERT INTO Involved (
			id_user,
			id_event)
		VALUES (?, ?)
	');
	
	if (!$new_envolved->execute(array($id_user,$id_event)))
		return -1;
	
	return 1;
}

/*
 * Description: Returns a list of all the visible events' id
 * Input: visibility:string, array_sports:ArrayOfString, id_user:int
 * Output: array_events_id:ArrayOfInt
*/
function GetEventsId($visibility, $sports_string, $id_user) {
	if (!($db = DataBaseConnection()))
		return -1;
		
	$array_sports = GetArrayFromString($sports_string);
	
	$array_events_id = array();
	
	$query_event = '
		SELECT id, owner, date_time 
		FROM  `Events` 
		WHERE  `visibility` LIKE  ? 
	';
	$order_by = ' ORDER BY date_time DESC';
	$query_sports = '';
	if (sizeof($array_sports) > 0) {
		$query_sports = $query_sports.' AND (';
		
		for ($i = 0; $i < sizeof($array_sports); $i++) {
			
			if ($i < sizeof($array_sports) - 1) {
				$query_sports = $query_sports.'`sport` LIKE ? OR ';
			} 
			else {
				$query_sports = $query_sports.'`sport` LIKE ?)';
			}
		}
		$query_event = $query_event.$query_sports;
	}
	
	$array_exe = array();
	$array_exe[] = $visibility;
	$array_exe = array_merge($array_exe,$array_sports);
	
	if ($visibility == 'public') {	
		
		$query_event = $db->prepare($query_event.$order_by);
		
		if (!($query_event->execute($array_exe)))
			return -1;
			
		while ($event = $query_event->fetch()) {
			$array_events_id[] = $event['id'];
		}
	
	}
	else if ($visibility == 'private') {
		$query = '
			SELECT * FROM (
			SELECT Private_Events.id, Private_Events.date_time 
			FROM
	        ('
			.$query_event.
			') AS Private_Events
			INNER JOIN 

			(SELECT Users.id_user
			FROM (
				SELECT id_user2 AS id_user
				FROM Friends
				WHERE id_user1 = ?
				AND state_request =  \'accepted\'
				
				UNION 
						
				SELECT id_user1 AS id_user
				FROM Friends
				WHERE id_user2 = ?
				AND state_request =  \'accepted\'
			) AS Friends_id
			INNER JOIN Users ON Friends_id.id_user = Users.id_user
			) AS Friends_ID
					
			ON Private_Events.owner = Friends_ID.id_user
			UNION
			SELECT id, date_time
			FROM Events
			WHERE owner = ? AND visibility = \'private\'
			'.$query_sports.') AS Result
			ORDER BY date_time DESC'
		;
		
		$array_id_user = array($id_user,$id_user, $id_user);
		$array_exe = array_merge($array_exe,$array_id_user);
		$array_exe = array_merge($array_exe,$array_sports);

		$query = $db->prepare($query);
		
		if (!($query->execute($array_exe)))
			return -1;

		while ($event = $query->fetch()) {
			$array_events_id[] = $event['id'];
		}
	}
	else
		return -1;
		
	return $array_events_id;
}

/*
 * Description: Returns all the involved users of an event
 * Input: id_event:int
 * Output: InvolvedUsers:String
*/
function GetInvolvedIn($id_event) {
	if (!($db = DataBaseConnection()))
		return -1;
		
	$query = '
		SELECT InvolvedUsers.id_user, Users.full_name, Users.url_avatar
		FROM (
   			SELECT id_user 
   			FROM Involved
   			WHERE id_event = ?
   		) AS InvolvedUsers
		INNER JOIN Users ON Users.id_user = InvolvedUsers.id_user
		ORDER BY InvolvedUsers.id_user DESC
	';
	
	$query = $db->prepare($query);
	if (!($query->execute(array($id_event))))
		return -1;
		
	$involved = '';
	
	$delimiter = 'x@x@x';
	while ($fetched = $query->fetch()) {
		$involved = $involved.$fetched['id_user'].$delimiter.utf8_encode($fetched['full_name']).$delimiter.$fetched['url_avatar'].$delimiter;
	}
	
	return $involved;
}

/*
 * Description: Returns a structure of an event
 * Input: id_event:int
 * Output: array_events_id:event(ComplexType:struct)
*/
function GetEvent($id_event) {
	if (!($db = DataBaseConnection()))
		return -1;
		
	$query = $db->prepare('SELECT * FROM Events WHERE id= ?');	
	if (!($query->execute(array($id_event))))
			return -1;
			
	$event = $query->fetch();
	
	$delimiter = '##b#a###';
	return $event['id'].$delimiter.utf8_encode($event['sport']).$delimiter.$event['owner'].$delimiter.$event['visibility'].$delimiter.$event['date_time'].$delimiter.$event['duration'].$delimiter.utf8_encode($event['location']).$delimiter.$event['longitude'].$delimiter.$event['latitude'].$delimiter.utf8_encode($event['description']);
}

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ USERS @@@@@@@@@@@@@@@@@@*/

/*
 * Description: Try to add an external user to the data base.The internal id 
 *				of the new user is returned. If the user was already 
 *				registered, the function just return his internal id.
 *				In case of fail, returns -1;
 * Input: $external_id:string, $log_manager:string, $full_name:string, 
 *		  $url_avatar:string, $gender:string, <br>
 *		  $date_of_birth:string
 * Output: id_user:int
*/
function AddUser($external_id, $log_manager, $full_name, $url_avatar, $gender, $date_of_birth) {
	if (!($db = DataBaseConnection()))
		return -1;
	
	if (($is_user = IsUser($log_manager,$external_id)) == -1)
		return -1;
		
	if ($is_user == 0) {
		$users = $db->prepare('INSERT INTO Users (
							  external_id,
							  log_manager,
							  full_name,
							  url_avatar,
							  gender,
							  date_of_birth
						   )
						   VALUES (?, ?, ?, ?, ?, ?)'
		);
		if ($users->execute(array($external_id, $log_manager, $full_name, $url_avatar, $gender, $date_of_birth)))
			return getInternalId($log_manager, $external_id);
		else
			return -1;
	}
	else
		return getInternalId($log_manager, $external_id);
}

/*
 * Description: returns all the informations about an user.
 *				returns null if this user does not exist.
 * Input: $id_user:int 
 * Output: user:struct(friend:ComplexType)
*/
function GetUser($id_user) {
	
	if (!$db = DataBaseConnection())
		return NULL;
		
	$user = $db->prepare('SELECT id_user, full_name, url_avatar, gender, date_of_birth FROM Users WHERE id_user = ?');
	if (!($user->execute(array($id_user))))
		return -1;
	
	$user = $user->fetch();
	
	return array(
			'id_user' => $user['id_user'],
			'full_name' => utf8_encode($user['full_name']),
			'url_avatar' => $user['url_avatar'],
			'gender' => $user['gender'],
			'date_of_birth' => $user['date_of_birth']
	);
}

/*
 * Description: Indicates whether the user with an external id from a certain
 *              log_manager already exists in the data base (returns 1) or not (returns 0).
 *				In case of failure, returns -1.
 * Input: $log_manager:String, $external_id:String
 * Output: is_user:int
*/
function IsUser($log_manager, $external_id) {
	if (!$db = DataBaseConnection())
		return -1;
		
	$users = $db->prepare('
		SELECT COUNT( * ) AS is_user
		FROM Users
		WHERE log_manager =  ?
		AND external_id =  ?
	');
	if (!$users->execute(array($log_manager,$external_id)))
		return -1;

	$user = $users->fetch();

	if ($user['is_user'] == 1)
		return 1;
	else 
		return 0;
}

/*
 * Description: Returns a list of the practiced sports by the user.
 *				It is returned in an array of string. The 0 and 
 				paired indices are the sports and the impaired
				indices are the level of the previous sport.
 * Input: $id_user:int
 * Output: practiced_list:arrayOfString
*/
function GetPracticedSports($id_user) {
	if (!$db = DataBaseConnection())
		return -1;
	
	$query = $db->prepare('SELECT sport, level FROM Practice WHERE id_user = ?');
	if (!($query->execute(array($id_user))))
		return -1;
	
	$practice = array();
	
	while ($fetched = $query->fetch()) {
		$practice[] = $fetched['sport'];
		$practice[] = $fetched['level'];
	}
	
	return $practice;
}

function AddPracticedSports($id_user, $str_sports, $str_levels) {
	if (!$db = DataBaseConnection())
		return -1;
		
	$array_sports = GetArrayFromString($str_sports);
	$array_levels= GetArrayFromString($str_levels);
	
	for ($i = 0; i < sizeof(str_sports); $i++) {
		$query = prepare('INSERT INTO Praticed (
			id_user, 
			sport,
			level)
			VALUES (?, ?, ?)
		');
		
		if (!($query->execute(array($id_user,$array_sports[$i],$array_levels[$i]))))
			return -1;
	}
}


/*
 * Description: returns the internal id of an user. In case of fail, returns -1.
 * Input: $log_manager:string, $external_id:string, $db:PDO
 * Output: id_user:int
*/
function GetInternalId($log_manager, $external_id) {
	if (!$db = DataBaseConnection())
		return -1;
		
	$id_user = $db->prepare('
		SELECT id_user
		FROM Users
		WHERE log_manager LIKE ?
		AND external_id LIKE ?
	');
	
	if ($id_user->execute(array($log_manager, $external_id))) {
		$id_user = $id_user->fetch();
		if ($id_user['id_user'])
			return $id_user['id_user'];
		else return -1;
	}
	else 
		return -1;
}

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ FRIENDS @@@@@@@@@@@@@@@@@@*/

/*
 * Description: Request to the data base all the friends of the input user.
 				Returns only the id_user
 * Input: $id_user:int
 * Output: array(friend:ComplexType)
*/
function GetFriendsOf($id_user) {
	if (!$db = DataBaseConnection())
		return -1;
		
	$friends = $db->prepare('
		SELECT *
		FROM (
			SELECT id_user2 AS id_user
			FROM Friends
			WHERE id_user1 = ?
			AND state_request =  \'accepted\'
			UNION 
			SELECT id_user1 AS id_user
			FROM Friends
			WHERE id_user2 = ?
			AND state_request =  \'accepted\'
		) AS Friends_id
		INNER JOIN Users ON Friends_id.id_user = Users.id_user
		');
	if (!($friends->execute(array($id_user,$id_user))))
		return -1;
	
	$array_friends = array();
	
	while ($friend = $friends->fetch()) {
		$array_friends[] = $friend['id_user'];
	}

	return $array_friends;
}

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ SPORTS @@@@@@@@@@@@@@@@@@@*/

/*
 * Description: Returns the list of available sports
 * Input: void
 * Output: array(string)
*/
function GetSports() {
	if (!$db = DataBaseConnection())
		return -1;
		
	$sql_query = $db->query('SELECT * FROM Sports');
	
	$sports = array();
	
	while ($sql_fetch = $sql_query->fetch()) {
		$sports[] = utf8_encode($sql_fetch['name']);
	}
	
	return $sports;
}

/*
 * Description: Returns the list of available levels
 * Input: void
 * Output: array(string)
*/
function GetLevels() {
	if (!$db = DataBaseConnection())
		return -1;
		
	$sql_query = $db->query('SELECT * FROM Levels');
	
	$levels = array();
	
	while ($sql_fetch = $sql_query->fetch()) {
		$levels[] = $sql_fetch['name'];
	}
	
	return $levels;
}

/************************************************************************************/
/********************************* PRIVATE FUNCTIONS ********************************/
/************************************************************************************/

/*
 * Description: connect to data base and return the connection for queries generation
 * Input: void
 * Output: $db:PDO
*/
function DataBaseConnection() {
	try {
		$db = new PDO('mysql:host=localhost;dbname=riosport', 'root', '');
		return $db;
	} catch (Exception $e) {
		try {
			$db = new PDO('mysql:host=localhost;dbname=pamaury', 'pamaury', 'RIOSPORT2014');
			return $db;
		} catch (Exception $e) {
			return null;
		}
	}
}

/*
 * Description: returns an array containing the element of the input string, assuming '$'
 * 				is the delimiter.
 * Input: str:string
 * Output: array:array(string)
*/

function GetArrayFromString ($str) {
	if ($str == '')
		return array();
	else
		return explode('$',$str);
}

include('soap_wsdl.php');

exit();
?>






