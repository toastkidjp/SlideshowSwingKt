Slide show
====

This tool can show slides with Markdown file.

# Getting start
TBD...

```
$ git clone git@github.com:toastkidjp/SlideshowSwingKt.git
$ gradle jar

// You should make any markdown file which is named "sample.md".

$ java -jar slideshow.swing.kt*.jar
```

## sample.md

```md
# TITOLI
@Leone

![background](C:/Users/any_user/Pictures/titoli.jpg)

## Normal slide
Ramon is so clever gunslinger.

# THE END
```

# License
This software is licensed with Eclipse Public License - v 1.0.

## Dependencies
This software contains following open source softwares. Thanks a lot. :bow:

| Name | License | Comment |
|:---|:---|:---|
| [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) | [BSD 3-Clause license](https://github.com/bobbylight/RSyntaxTextArea/blob/3.1.1/RSyntaxTextArea/src/main/resources/META-INF/LICENSE) | Use for displaying code.
| [PDFBox](https://pdfbox.apache.org/) | Apache License 2.0 | Converting slides to PDF

