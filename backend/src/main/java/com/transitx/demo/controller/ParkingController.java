package com.transitx.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.*;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
  private static final Path UPLOAD_DIR = Paths.get("uploads");

  @PostMapping("/uploadDoc")
  public @ResponseBody String upload(@RequestParam("file") MultipartFile file) throws Exception {
    boolean vuln = "on".equalsIgnoreCase(System.getenv().getOrDefault("VULN_MODE", "off"));
    Files.createDirectories(UPLOAD_DIR);

    String name = file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename();
    Path target = UPLOAD_DIR.resolve(name);

    if (!vuln) {
      target = target.normalize().toAbsolutePath();
      Path root = UPLOAD_DIR.toAbsolutePath();
      if (!target.startsWith(root)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad path");
      // enforce simple whitelist
      if (!(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".pdf"))) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only png/jpg/pdf allowed");
      }
    }
    file.transferTo(target);
    return target.toString();
  }
}
