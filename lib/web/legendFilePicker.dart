import 'dart:io';

import 'package:file_picker/file_picker.dart';

class LegendFilePicker {
  static Future<Map> pickSingleFile({
    List<String>? extensions,
  }) async {
    FilePickerResult? result;
    result = extensions == null
        ? await FilePicker.platform.pickFiles()
        : await FilePicker.platform.pickFiles(
            type: FileType.custom,
            allowedExtensions: extensions,
          );

    if (result != null) {
      PlatformFile pf = result.files.single;

      return {
        "bytes": pf.bytes,
        "name": pf.name,
        "path": pf.path,
      };
    } else {
      // User canceled the picker
      print("Canceled");
      return {"bytes": null};
    }
  }

  static Future<List<Map?>> pickMultipleFiles(
      {List<String>? extensions}) async {
    FilePickerResult? result;
    result = extensions == null
        ? await FilePicker.platform.pickFiles(
            allowMultiple: true,
          )
        : await FilePicker.platform.pickFiles(
            allowMultiple: true,
            type: FileType.custom,
            allowedExtensions: extensions,
          );

    if (result != null) {
      List<Map> files = result.files
          .map(
            (f) => {
              "bytes": f.bytes,
              "name": f.name,
              "path": f.path,
            },
          )
          .toList();
      return files;
    } else {
      // User canceled the picker
      print("Canceled");
      return [];
    }
  }
}
