#!/usr/bin/env python
#coding: utf-8

####
#
# Import
#
####
import pyjsonrpc
import json
import app

####
#
# Global Variables
#
####
print "%%%%%%%%%%%%%%%%%%%%%%%%%%INIT SERVER%%%%%%%%%%%%%%%%%%%%%%%%%%"
#Les adresses ip doivent etre des str
# ip of the Raspberry Pi Host
# correspond a la clef RASP
RASP_IP = "192.168.43.143" 

# ip of one instance of xbmc
MAIN_IP= "192.168.43.29"
Robin="RobinS"
#Robin="Walid"
Remi="REMI-PC"
RASP = "Mehdi"

Perif_dico = {RASP : RASP_IP,"MAIN" :MAIN_IP,Robin:"192.168.43.102","Simba":"192.168.43.29",Remi:"192.168.43.35","SPEINFO-THINK2":"192.168.43.188"}

CURRENT_IP = Perif_dico[RASP]

# Listenned port
SERVER_PORT = 50420 #doit etre un int

####
#
# Server class handling Android request
# to control xbmc media center
#
####
class RequestHandler(pyjsonrpc.HttpRequestHandler):

	# Fonction pausing/playing the music
	@pyjsonrpc.rpcmethod
	def playpause(self):
		xbmc = app.XBMCManager(CURRENT_IP)	
		result= xbmc.playPause()
		xbmc.connection.close()
		return result
		
	# Fonction used to get music content on the xbmc library
	@pyjsonrpc.rpcmethod
	def getaudio(self,devicename):
		print "devicename :", devicename
		global CURRENT_IP
		CURRENT_IP = Perif_dico[devicename]
		xbmc = app.XBMCManager(CURRENT_IP)	
		result= xbmc.getAudio()
		xbmc.connection.close()
		return result

	# Function used to get video content on the xbmc library
	@pyjsonrpc.rpcmethod
	def getvideo(self):
		xbmc = app.XBMCManager(CURRENT_IP)	
		result= xbmc.getVideo()
		xbmc.connection.close()
		return result

	@pyjsonrpc.rpcmethod
	def getmedia(self,devicename):
		print "devicename :", devicename
		global CURRENT_IP
		CURRENT_IP = Perif_dico[devicename]
		xbmc = app.XBMCManager(CURRENT_IP)	
		result= xbmc.getMedia()
		xbmc.connection.close()
		return result


		
	
	# Fonction used to start a music
	@pyjsonrpc.rpcmethod
	def play(self, artist, id):
		print CURRENT_IP
		xbmc = app.XBMCManager(CURRENT_IP)	
		result= xbmc.play(artist, id)
		xbmc.connection.close()
		print "%%%%%%%%%%%PLAY%%%%%%%\n",result
		return result

	@pyjsonrpc.rpcmethod
	def switch(self, devicename):
		global CURRENT_IP
		xbmc = app.XBMCManager(CURRENT_IP)
		xbmc.playPause()
		print "Getting statu of : ",CURRENT_IP
		CURRENT_STATUT=xbmc.getCurrentPlayerState()
		xbmc.stop()
		xbmc.connection.close()
		CURRENT_IP=Perif_dico[devicename]
		print "SWITCH to : ",CURRENT_IP
		xbmc = app.XBMCManager(CURRENT_IP)
		result=xbmc.setPlayerState(CURRENT_STATUT)
		xbmc.connection.close()
		return result

	# Fonction used to test the connexion between the server 
	# and the remote control 
	@pyjsonrpc.rpcmethod
        def add(self, a, b):
                return a+b
	
	# Fonction used to test an error's case
	@pyjsonrpc.rpcmethod
	def error(self,a,b):
		try:
			x=1/0
		except ZeroDivisionError as detail:
			print "Handling error" , detail	
	@pyjsonrpc.rpcmethod
	def stop(self):
		xbmc = app.XBMCManager(CURRENT_IP)	
		result= xbmc.stop()
		xbmc.connection.close()
		return result
		



# Threading HTTP-Server
http_server = pyjsonrpc.ThreadingHttpServer(

	# xbmc use port 8080 / Try to use another
	server_address = (RASP_IP, SERVER_PORT),
	RequestHandlerClass = RequestHandler
)



####
#
# MAIN
#
####

try:
	print "Serveur is listenning on ", RASP_IP, " on port ", SERVER_PORT
    	http_server.serve_forever()
except KeyboardInterrupt:
    	http_server.shutdown()
	print "Stopping HTTP server ..."


