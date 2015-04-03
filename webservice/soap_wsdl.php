<?php

/*
 * Name: server.php
 * Created by Rémi Prévost on 07/11/2014.
 * for RioSport web service provider
*/

/***************************************/
/********** SOAP/WSDL CONFIG ***********/
/***************************************/

require_once('nusoap.php');

$NAMESPACE = 'https://www.etud.insa-toulouse.fr/~pamaury/webService';
$temp ='http://localhost/RioSportWebService';
$server = new soap_server;

/* Initialise le support WSDL */
$server->methodreturnisliteralxml = false;
$server->soap_defencoding = 'UTF-8';
$server->configureWSDL('RioSportWebService', $NAMESPACE);
$server->wsdl->schemaTargetNamespace = $NAMESPACE;

/************************************************************************************/
/********************************** COMPLEX TYPES ***********************************/
/************************************************************************************/


/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ FRIENDS @@@@@@@@@@@@@@@@@@*/

$server->wsdl->addComplexType(
	'friend',
	'complexType',
	'struct',
	'all',
	'',
	array(
		'id_user' => array('name' => 'id_user', 'type' => 'xsd:int'),
		'full_name' => array('name' => 'full_name', 'type' => 'xsd:string'),
		'url_avatar' => array('name' => 'url_avatar', 'type' => 'xsd:string'),
		'gender' => array('name' => 'gender', 'type' => 'xsd:string'),
		'date_of_birth' => array('name' => 'date_of_birth', 'type' => 'xsd:string')
	)
);

$server->wsdl->addComplexType(
  'ArrayOfInt',
  'complexType',
  'array',
  'sequence',
  '',
  array(
    'itemName' => array(
      'name' => 'itemName', 
      'type' => 'xsd:int',
      'minOccurs' => '0', 
      'maxOccurs' => 'unbounded'
    )
  )
);

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ SPORTS @@@@@@@@@@@@@@@@@@@*/

$server->wsdl->addComplexType(
  'ArrayOfString',
  'complexType',
  'array',
  'sequence',
  '',
  array(
    'itemName' => array(
      'name' => 'itemName', 
      'type' => 'xsd:string',
      'minOccurs' => '0', 
      'maxOccurs' => 'unbounded'
    )
  )
);


/************************************************************************************/
/******************************** FUNCTIONS REGISTER ********************************/
/************************************************************************************/

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ TESTS @@@@@@@@@@@@@@@@@@*/

$server->register('sayHello',           /* method name */
   array('input' => 'xsd:string'),    /* input parameters */
   array('return' => 'xsd:string'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#sayHello', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'Return the square of a number'         /* documentation */
);

$server->register('Square',           /* method name */
   array('input' => 'xsd:int'),    /* input parameters */
   array('return' => 'xsd:int'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#Square', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'Return the square of a number'         /* documentation */
);

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ EVENTS @@@@@@@@@@@@@@@@@@*/

$server->register('AddEvent',           /* method name */
   array('sport' => 'xsd:string', 'owner' => 'xsd:int', 'visibility' => 'xsd:string', 'date_time' => 'xsd:string', 'duration' => 'xsd:int', 'location' => 'xsd:string',  'longitude' => 'xsd:float', 'latitude' => 'xsd:float', 'description' => 'xsd:string'),    /* input parameters */
   array('return' => 'xsd:int'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#AddEvent', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'Add a new event in the data base'         /* documentation */
);


$server->register('GetEventsId',           /* method name */
   array('visibility' => 'xsd:string', 'sports_string' => 'xsd:string', 'id_user' => 'xsd:int'),    /* input parameters */
   array('return' => 'tns:ArrayOfInt'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#GetEventsId', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                         /* use */
   'Returns a list of all the visible events\' id'         /* documentation */
);

$server->register('GetEvent',           /* method name */
   array('id_event' => 'xsd:int'),    /* input parameters */
   array('return' => 'xsd:string'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#GetEvent', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                         /* use */
   'Returns a structure of an event'         /* documentation */
);

$server->register('GetInvolvedIn',           /* method name */
   array('id_event' => 'xsd:int'),    /* input parameters */
   array('return' => 'xsd:string'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#GetInvolvedIn', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                         /* use */
   'Returns all the involved users of an event'         /* documentation */
);

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ USERS @@@@@@@@@@@@@@@@@@*/

$server->register('AddUser',           /* method name */
   array('external_id' => 'xsd:string', 'log_manager' => 'xsd:string', 'full_name' => 'xsd:string', 'url_avatar' => 'xsd:string', 'gender' => 'xsd:string', 'date_of_birth' => 'xsd:string'),    /* input parameters */
   array('return' => 'xsd:int'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#AddUser', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'Try to add an external user to the data base.The internal id of the new user is returned. If the user was already registered, the function just return his internal id. In case of fail, returns -1;'         /* documentation */
);

$server->register('IsUser',           /* method name */
   array('log_manager' => 'xsd:string', 'external_id' => 'xsd:string'),    /* input parameters */
   array('return' => 'xsd:int'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#IsUser', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'indicates whether the input external user is already member of RioSport'         /* documentation */
);

$server->register('GetUser',           /* method name */
   array('id_user' => 'xsd:int'),    /* input parameters */
   array('return' => 'tns:friend'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#GetUser', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'Returns all the informations about an user. Returns null if this user does not exist'         /* documentation */
);

$server->register('GetPracticedSports',           /* method name */
   array('id_user' => 'xsd:int'),    /* input parameters */
   array('return' => 'tns:ArrayOfString'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#GetPracticedSports', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'Returns a list of the practiced sports by the user. It is returned in an array of string. The 0 and paired indices are the sports and the impaired indices are the level of the previous sport.'         /* documentation */
);

$server->register('GetInternalId',           /* method name */
   array('log_manager' => 'xsd:string', 'external_id' => 'xsd:string'),    /* input parameters */
   array('return' => 'xsd:int'),    /* output parameters */
   $NAMESPACE,          /* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#GetInternalId', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'returns the internal id of an user. In case of fail, returns -1.'         /* documentation */
);

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ FRIENDS @@@@@@@@@@@@@@@@@@*/

$server->register('GetFriendsOf',           /* method name */
   array('id_user' => 'xsd:int'),    /* input parameters */
   array('return' => 'tns:ArrayOfInt'),    /* output parameters */
   $NAMESPACE,						/* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#GetFriendsOf', /* soapaction (fonction) */
  'rpc',                              /* style */
   'encoded',                          /* use */
   'Return all the friends of the requested user'         /* documentation */
);

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@ SPORTS @@@@@@@@@@@@@@@@@@@*/

$server->register('GetSports',           /* method name */
   array(),    /* input parameters */
   array('return' => 'tns:ArrayOfString'),    /* output parameters */
   $NAMESPACE,						/* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#GetSports', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'Returns the list of available sports'         /* documentation */
);

$server->register('GetLevels',           /* method name */
   array(),    /* input parameters */
   array('return' => 'tns:ArrayOfString'),    /* output parameters */
   $NAMESPACE,						/* namespace (espace de nommage unique) */
   'https://www.etud.insa-toulouse.fr/~pamaury/webService#GetLevels', /* soapaction (fonction) */
   'rpc',                              /* style */
   'encoded',                          /* use */
   'Returns the list of available sports'         /* documentation */
);



$server->service(isset($GLOBALS['HTTP_RAW_POST_DATA']) ? $GLOBALS['HTTP_RAW_POST_DATA'] : '');
?>