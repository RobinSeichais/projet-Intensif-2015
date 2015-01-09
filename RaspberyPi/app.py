import httplib
import urllib
import json
import math

#######
#
#	AVAILABLE METHODS:
#
#	switchDevice(ip)
#
#	executeJSONRPC(request)
#
#	doSomething()
#
#	playPause()
#
#	stop()
#
#	getAudio()
#
#	play(artist, id)
#
#	getCurrentPlayerState()
#
#	setPlayerState(state)
#
#######



class XBMCManager:

	###
	#	CONSTRUCTOR METHOD
	#
	#	INITIALIZE THE CONNECTION TO THE XBMC WITH GIVEN IP ADDRESS
	###
	def __init__(self, ip):

		#DEBUG
		print '__init__() - ' + ip + ':8080'

		self.connection = httplib.HTTPConnection(ip, 8080)



	#	SWITCH USED DEVICE TO THE ONE WITH THE IP ADRESS GIVEN
	def switchDevice(self, ip):

		#DEBUG
		print 'switchDevice(ip) - ' + ip

		self.connection.close()
		self.connection = httplib.HTTPConnection(ip, 8080)



	#	EXECUTE JSON-RPC REQUEST PASSED AS PARAMETER
	def executeJSONRPC(self, request):

		#DEBUG
		print 'executeJSONRPC(request) - ' + request

		self.connection.request('GET', '/jsonrpc?request=' + urllib.quote(request, ''))

		response = self.connection.getresponse()
		if response.status == httplib.OK:
			return True
		return False


	#	CREATE JSON-RPC REQUEST FROM METHOD NAME, PARAMETERS AND ID
	def _createJsonrpc(self, method, params, i): 
		return '{"jsonrpc":"2.0", "method":"' + method + '", "params":' + params + ', "id": "' + i + '"}'


	#	SEND POST REQUEST FROM GIVEN METHOD NAME, PARAMETERS AND ID
	def _sendRequest(self, method, params ,i):
		self.connection.request('POST', '/jsonrpc', self._createJsonrpc(method,params,i))



	#	SHOW NOTIFICATION
	def doSomething(self):

		print 'doSomething()'
		self._sendRequest('GUI.ShowNotification','{"title":"Hey!", "message":"Doing something !"}', 'doSomething' )        
		response = self.connection.getresponse()

		if response.status == httplib.OK:
			return True
		elif response.status == httplib.BAD_REQUEST:
			return False
		return False



	#	PLAY/PAUSE CURRENT PLAYER
	def playPause(self):

		#DEBUG
		print 'playPause()'

		player_id = self._getActivePlayer()

		self._sendRequest('Player.PlayPause', '{"playerid": ' + str(player_id) + '}', 'playPause')    
		response = self.connection.getresponse()
        	response.read()
		if response.status == httplib.OK:
			return True
		elif response.status == httplib.BAD_REQUEST:
			return False
		return False


	#	STOP PLAYER
	def stop(self):

		#DEBUG
		print 'stop()'

		player_id = self._getActivePlayer()

		self._sendRequest('Player.Stop', '{"playerid": ' + str(player_id) + '}', 'playPause')    
		response = self.connection.getresponse()
        
		if response.status == httplib.OK:
			return True
		elif response.status == httplib.BAD_REQUEST:
			return False
		return False


	#	RETURN ID OF CURRENTLY ACTIVE PLAYER: 0 IF AUDIO PLAYER / 1 IF VIDEO PLAYER [ / 2 IF PHOTO PLAYER]
	def _getActivePlayer(self):

		#DEBUG
		print 'getActivePlayer()'

		request = '{"jsonrpc": "2.0", "method": "Player.GetActivePlayers", "id": 1}'
	
		self.connection.request('POST', '/jsonrpc', request)

		response = self.connection.getresponse()
		if response.status == httplib.OK:
			data = json.loads(response.read())
			#conn.close()
			if data['result']:
				player_id = data['result'][0]["playerid"]
				print "On est dans les ifs%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
				return player_id

	def getMedia(self):

		return '{' + self._getAudio() + ', ' + self._getVideo() + '}'

	def getAudio(self):

		return '{' + self._getAudio() + '}'

	#	RETRIEVE AND PARSE ALL AUDIO CONTENT TO BE SEND TO THE ANDROID REMOTE
	def _getAudio(self):

		#DEBUG
		print 'getAudio()'

		self._sendRequest('AudioLibrary.GetSongs', '{ "limits": { "start" : 0 }, "properties": ["artist"] }', 'retrieveSongs')
		response = self.connection.getresponse()

		if response.status == httplib.OK:

			result = '"audio":['
			json_data = json.loads(response.read())
			first = True
			for song in json_data['result']['songs']:
				
				if song['artist'] == []:
					song['artist'].append('Unknown')

				if first:
					first = False
					result += '{"title":"' + song['label'] + '", '
					result += '"artist":"' + song['artist'][0] + '", '
					result += '"id":' + str(song['songid']) + '}'
				else:
					result += ', '
					result += '{"title":"' + song['label'] + '", '
					result += '"artist":"' + song['artist'][0] + '", '
					result += '"id":' + str(song['songid']) + '}'

			result += ']'

			return result

	def getVideo(self):

		return '{' + self._getVideo() + '}'


	#	@TODO:	RETRIEVE AND PARSE ALL VIDEO CONTENT
	def _getVideo(self):

		#DEBUG
		print 'getVideo()'

		request = '{"jsonrpc": "2.0", "method": "VideoLibrary.GetMovies", "params": { "limits": { "start" : 0}, "properties": ["title"]}, "id": "libTvShows"}'
		
		self.connection.request('GET', '/jsonrpc?request=' + urllib.quote(request, ''))

		response = self.connection.getresponse()
		if response.status == httplib.OK:
			
			result = '"video":['
			json_data = json.loads(response.read())

			first = True
			for movie in json_data['result']['movies']:
				
				if first:
					first = False
					result += '{"title":"' + movie['title'] + '", '
					result += '"id":' + str(movie['movieid']) + '}'
				else:
					result += ', '
					result += '{"title":"' + movie['title'] + '", '
					result += '"id":' + str(movie['movieid']) + '}'

			result += ']'

			return result
			

	#	PLAY SONG FROM ARTIST NAME AND SONG ID
	def play(self, artist, id):

		path = self._findSongFromID(artist, id)
		return self._play(path)
		

	#	PLAY VIDEO FROM MOVIE ID
	def playVideo(self, id):

		path = self._findMovieFromID(id)
		return self._play(path)


	#	PLAY SONG FROM PATH 
	def _play(self, path):

		#DEBUG
		print 'play() - ' + path

		req1 = '{"jsonrpc": "2.0", "method": "Playlist.Clear", "params":{"playlistid":1}, "id": "clearPlaylist"}'
		req2 = '{"jsonrpc": "2.0", "method": "Playlist.Add", "params":{"playlistid":1, "item" :{ "file" : "' + path + '"}}, "id" : "addToPlaylist"}'
		req3 = '{"jsonrpc": "2.0", "method": "Player.Open", "params":{"item":{"playlistid":1, "position" : 0}}, "id": "openPlaylist"}'
						
		self.connection.request('POST', '/jsonrpc', req1)

		response = self.connection.getresponse()
		if response.status == httplib.OK:
			print response.read()
			
			self.connection.request('POST', '/jsonrpc', req2)

			response = self.connection.getresponse()
			if response.status == httplib.OK:
				print response.read()
				
				self.connection.request('POST', '/jsonrpc', req3)

				response = self.connection.getresponse()
				if response.status == httplib.OK:
					print response.read()
					return True
		return False


	#	FIND PATH OF A SONG FROM ARTIST NAME AND SONG ID
	def _findSongFromID(self, artist, id):

		#DEBUG
		print '_findSongFromID() - ' + artist + ' - ' + str(id)

		request = '{"jsonrpc": "2.0", "method": "AudioLibrary.GetSongs", "params": { "limits": { "start" : 0 }, "properties": ["artist", "file"], "filter": {"artist": "' + artist + '"}}, "id": "findSong"}'

		self.connection.request('POST', '/jsonrpc', request)

		response = self.connection.getresponse()
		if response.status == httplib.OK:
			#print response.read()
			
			json_data = json.loads(response.read())

			for song in json_data['result']['songs']:
				if song['songid'] == id :
					return song['file']
		return "NOT_FOUND"


	#	FIND SONG FROM ARTIST NAME AND TITLE
	def _findSongFromTitle(self, artist, title):

		#DEBUG
		print '_findSongFromTitle() - ' + artist + ' - ' + title

		request = '{"jsonrpc": "2.0", "method": "AudioLibrary.GetSongs", "params": { "limits": { "start" : 0 }, "properties": ["artist", "file"], "filter": {"artist": "' + artist + '"}}, "id": "findSong"}'

		self.connection.request('POST', '/jsonrpc', request)

		response = self.connection.getresponse()
		if response.status == httplib.OK:
			json_data = json.loads(response.read())

			for song in json_data['result']['songs']:
				if song['label'] ==  title:
					return song['file']
		return "NOT FOUND"


	#	FIND PATH OF A MOVIE FROM MOVIE ID
	def _findMovieFromID(self, id):

		#DEBUG
		print '_findMovieFromID() - ' + str(id)

		request = '{"jsonrpc": "2.0", "method": "VideoLibrary.GetMovies", "params": { "limits": { "start" : 0}, "properties": ["title", "file"]}, "id": "libTvShows"}'
		
		self.connection.request('POST', '/jsonrpc', request)

		response = self.connection.getresponse()
		if response.status == httplib.OK:
			
			json_data = json.loads(response.read())

			for movie in json_data['result']['movies']:
				if movie['movieid'] == id :
					return movie['file']
			return "NOT_FOUND"				



	#	FIND MOVIE FROM TITLE
	def _findMovieFromTitle(self, title):

		#DEBUG
		print '_findMovieFromTitle() - ' + title

		request = '{"jsonrpc": "2.0", "method": "VideoLibrary.GetMovies", "params": { "limits": { "start" : 0}, "properties": ["title", "file"]}, "id": "libTvShows"}'
		
		self.connection.request('POST', '/jsonrpc', request)

		response = self.connection.getresponse()
		if response.status == httplib.OK:
			
			json_data = json.loads(response.read())

			for movie in json_data['result']['movies']:
				if movie['title'] == title :
					return movie['file']
			return "NOT_FOUND"	


	#	RETURNS INFORMATION ON CURRENT PLAYER: TITLE AND ARTIST OF CURRENTLY PLAYED TRACK AND RUN TIME
	def getCurrentPlayerState(self):

		#DEBUG
		print 'getCurrentState()'

		player_id = self._getActivePlayer()
		#player_id =0# self._getActivePlayer()
		print "%%%%%%%%%%%%%%%%%%%%%%%%%% player_id",player_id
		type = ""
		title = ""
		artist = ""
		time = ""

		request = '{"jsonrpc": "2.0", "method": "Player.GetItem", "params": { "properties": ["title", "artist"], "playerid": ' + str(player_id) + ' }, "id": "getSongPlaying"}'
		
		self.connection.request('POST', '/jsonrpc', request)
		response = self.connection.getresponse()
		
		if response.status == httplib.OK:
			data = json.loads(response.read())['result']['item']
			title = data['title']
			if data['artist'] == []:
				data['artist'].append("Unknown")
			artist = data['artist'][0]

		request = '{"jsonrpc": "2.0", "method": "Player.GetProperties", "params": { "properties": ["time", "totaltime"], "playerid": ' + str(player_id) + ' }, "id": "getCurrentTime"}'
		
		self.connection.request('POST', '/jsonrpc', request)
		response = self.connection.getresponse()
		
		if response.status == httplib.OK:
			data = json.loads(response.read())['result']

			totalS = data['totaltime']['seconds']
			totalM = data['totaltime']['minutes']
			totalH = data['totaltime']['hours']
			currentS = data['time']['seconds']
			currentM = data['time']['minutes']
			currentH = data['time']['hours']

			total = totalS + totalM*60 + totalH*3600
			current = currentS + currentM*60 + currentH*3600

			time = math.floor(current*100/total)

		if player_id == 0:
			type = "audio"
		else:
			type = "video"

		return '{"type": "' + type + '", "title":"' + title + '", "artist":"' + artist + '", "time":' + str(time) + '}'


	#	SET THE PLAYER THE GIVEN STATE
	def setPlayerState(self, state):

		#DEBUG
		print 'setPlayerState() - ' + state

		data = json.loads(state)
		type = data['type']
		title = data['title']
		artist = data['artist']
		time = data['time']

		if type == "audio":
			path = self._findSongFromTitle(artist, title)
			self._play(path)
		else:
			path = self._findMovieFromTitle(title)
			self._play(path)

		player_id = self._getActivePlayer()

		#REQUIRE PERCENTAGE OF ADVANCEMENT
		request = '{"jsonrpc": "2.0", "method": "Player.Seek", "params": { "value": ' + str(time) + ', "playerid": ' + str(player_id) + ' }, "id": "getSongPlaying"}'
		
		self.connection.request('POST', '/jsonrpc', request)
		response = self.connection.getresponse()
		
		if response.status == httplib.OK:
			print response.read()
			return True
		return False


