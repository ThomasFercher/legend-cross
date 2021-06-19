import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:legend_interface/legend_interface.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  Map<dynamic, dynamic>? _file;
  List? _files;

  @override
  void initState() {
    super.initState();
    //initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await LegendInterface.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> pickFile() async {
    Map<dynamic, dynamic>? file;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      file = await LegendInterface.pickFile();
    } on PlatformException {
      file = null;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _file = file;
    });
  }

  Future<void> pickMultipleFiles() async {
    List? files;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      files = await LegendInterface.pickMultipleFiles();
    } on PlatformException {
      files = null;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _files = files;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            TextButton(
                onPressed: () => {
                      initPlatformState(),
                    },
                child: Text("test")),
            TextButton(
                onPressed: () => {
                      pickFile(),
                    },
                child: Text("pickFile")),
            TextButton(
                onPressed: () => {
                      pickMultipleFiles(),
                    },
                child: Text("pickFile")),
            Center(
              child: Text('Running on: $_platformVersion\n'),
            ),
            Center(
              child: Text(_file?["name"] ?? "Nothing"),
            ),
            Center(
              child: Text(_files?[0]["name"] ?? "Nothing"),
            ),
            Center(
              child: Text(_files?[1]["name"] ?? "Nothing"),
            ),
          ],
        ),
      ),
    );
  }
}
