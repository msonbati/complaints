/* webkitURL is deprecated but nevertheless */
URL = window.URL || window.webkitURL;

var gumStream;
var rec;
var input;


var AudioContext = window.AudioContext || window.webkitAudioContext;
var audioContext /* audio context to help us record */

var recordButton = document.getElementById("recordButton");
var stopButton = document.getElementById("stopButton");
//var pauseButton = document.getElementById("pauseButton");


recordButton.addEventListener("click", startRecording);
stopButton.addEventListener("click", stopRecording);
//pauseButton.addEventListener("click", pauseRecording);

function startRecording() {


	/*
		Simple constraints object, for more advanced audio features see
		https://addpipe.com/blog/audio-constraints-getusermedia/
	*/

    var constraints = { audio: true, video:false }

 	/*
    	Disable the record button until we get a success or fail from getUserMedia()
	*/

	recordButton.disabled = true;
	stopButton.disabled = false;
	//pauseButton.disabled = false

	/*
    	We're using the standard promise based getUserMedia()
    	https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices/getUserMedia
	*/

	navigator.mediaDevices.getUserMedia(constraints).then(function(stream) {
	//	console.log("getUserMedia() success, stream created, initializing Recorder.js ...");

		/*
			create an audio context after getUserMedia is called
			sampleRate might change after getUserMedia is called, like it does on macOS when recording through AirPods
			the sampleRate defaults to the one set in your OS for your playback device
		*/
		audioContext = new AudioContext();

		/* update the format */
		document.getElementById("formats").innerHTML="Format: 1 channel pcm @ "+audioContext.sampleRate/1000+"kHz"

		/*  assign to gumStream for later use  */
		gumStream = stream;

		/* use the stream */
		input = audioContext.createMediaStreamSource(stream);

		/*
			Create the Recorder object and configure to record mono sound (1 channel)
			Recording 2 channels  will double the file size
		*/
		rec = new Recorder(input,{numChannels:1})

		/* start the recording process */
		rec.record()

		console.log("Recording started");

	}).catch(function(err) {
	  	/* enable the record button if getUserMedia() fails */
    	recordButton.disabled = false;
    	stopButton.disabled = true;
    //	pauseButton.disabled = true
	});
}
/*
function pauseRecording(){
	console.log("pauseButton clicked rec.recording=",rec.recording );
	if (rec.recording){

		rec.stop();
		pauseButton.innerHTML="Resume";
	}else{

		rec.record()
		pauseButton.innerHTML="Pause";

	}
}
*/
function stopRecording() {
	//console.log("stopButton clicked");

	/* disable the stop button, enable the record too allow for new recordings */
	stopButton.disabled = true;
	recordButton.disabled = false;
//	pauseButton.disabled = true;

	/* reset button just in case the recording is stopped while paused */
	//pauseButton.innerHTML="Pause";

	/* tell the recorder to stop the recording */
	rec.stop();

	/* stop microphone access */
	gumStream.getAudioTracks()[0].stop();

	/* create the wav blob and pass it on to createDownloadLink */
	//rec.exportWAV(createDownloadLink);
	rec.exportWAV(handleBlob);
}

function handleBlob(blob){
    var url = URL.createObjectURL(blob);
    var au = document.createElement('audio');
	au.controls = true;
	au.src = url;
    var reader = new FileReader();
    var base64data;
    reader.readAsDataURL(blob);
    reader.onloadend = function() {
      base64data = reader.result;
      //console.log(base64data);
      document.getElementById("recording").value = base64data;
  }
}

function createDownloadLink(blob) {
	var url = URL.createObjectURL(blob);
	var au = document.createElement('audio');
	var li = document.createElement('li');
	var link = document.createElement('a');
	var filename = new Date().getTime()+".wav";
	au.controls = true;
	au.src = url;
	li.appendChild(au);
	li.appendChild(document.createTextNode(filename))
	//li.appendChild(link);
	var upload = document.createElement('a');
	upload.href="#";
    upload.setAttribute("style", "color:green; font-size:2em;");
	upload.innerHTML = "Attach Audio ارفاق ملف الصوت";
	upload.addEventListener("click", function(event){
		  var xhr=new XMLHttpRequest();
		  xhr.onload=function(e) {
		      if(this.readyState === 4) {
		          console.log("Server returned: ",e.target.responseText);
		      }
		  };
		  var fd=new FormData();
		  fd.append("audio_data",blob, filename);
		  xhr.open("POST","/upload",true);
		  xhr.send(fd);
	})
	li.appendChild(document.createTextNode (" "));
	li.appendChild(upload);
	recordingsList.appendChild(li);

}