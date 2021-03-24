import 'dart:async';

import 'package:file/local.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_audio_recorder/flutter_audio_recorder.dart';
import 'package:phone_state_i/phone_state_i.dart';

class HomePage extends StatefulWidget {
  @override
  _HomePageState createState() => new _HomePageState();
}

class _HomePageState extends State<HomePage> {
  StreamSubscription streamSubscription;
  FlutterAudioRecorder recorder;
  LocalFileSystem localFileSystem = LocalFileSystem();
  int status = 0;
  static const platform = const MethodChannel('recordChannel');

  @override
  Widget build(BuildContext context) {
    return new Scaffold();
  }

  @override
  void dispose() {
    super.dispose();
    streamSubscription.cancel();
  }

  @override
  void initState() {
    super.initState();
    streamSubscription = phoneStateCallEvent.listen((PhoneStateCallEvent event) async {
      print('Call is Incoming or Connected ' + event.stateC);

      if (event.stateC == "true") {
        await start();
      } else {
        await stop();
      }

      var path = "/storage/emulated/0/${DateTime.now()}";
      recorder = FlutterAudioRecorder(path, audioFormat: AudioFormat.AAC); // or AudioFormat.WAV
    });
  }

  Future<void> stop() async {
    status = 0;
    try {
      final bool result = await platform.invokeMethod('startRecord');
    } on PlatformException catch (e) {}
  }

  Future<void> start() async {
    status = 1;
    try {
      final bool result = await platform.invokeMethod('stopRecord');
    } on PlatformException catch (e) {}
  }
}
