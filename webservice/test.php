<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
	</head>

<?php

print_r(GetEventsId('private','',35));

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

/**********************************/
/******* PRIVATE FUNCTIONS ********/
/**********************************/


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

function GetArrayFromString ($str) {
	if ($str == '')
		return array();
	else
		return explode('$',$str);
}

?>