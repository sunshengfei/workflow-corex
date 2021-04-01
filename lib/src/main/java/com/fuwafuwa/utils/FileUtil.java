package com.fuwafuwa.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileUtil {

    public static final int WHAT_COPY_START = 0x51;
    public static final int WHAT_COPY_PROGRESS = 0x52;
    public static final int WHAT_COPY_FINISH = 0x53;


    public static String sizeString(long fileLength) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int index = 0;
        double realSize = fileLength;
        if (realSize >= 1024) {
            while (index < units.length && realSize >= 1024) {
                realSize = realSize / 1024d;
                index++;
            }
        }
        String string = String.format(Locale.getDefault(), "%.2f%s", realSize, units[index]);
        String[] numbers = string.split("\\.");
        if (numbers.length > 1) {
            numbers[1] = numbers[1].replaceAll("0+$", "");
        }
        return StringUtils.join(numbers, ".");

    }

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @return 创建成功：true，创建失败：false
     */
    public static boolean mkdir(String path) {
        boolean ret = false;
        File myDir = new File(path);
        if (myDir.isDirectory()) return true;
        if (!myDir.exists()) {
            ret = myDir.mkdirs();
        }
        return ret;
    }

    /**
     * 创建文件
     *
     * @param path 文件全路径
     * @param text 若不为空，则在新创建的文件中加入这些字符，这样就是文本文件了
     * @throws IOException
     */

    public static File createFile(String path, String filename, String text)
            throws IOException {
        File myFile = new File(path, filename);
        if (!myFile.exists()) {
            myFile.createNewFile();
        }
        if (!TextUtils.isEmpty(text)) {
            rewriteFile(myFile, text);
        }
        return myFile;
    }

    /**
     * 复写文件，新内容会替代旧内容
     *
     * @param file 要写入到的文件（受体）
     * @param text 要被写入的内容（想不起了受体对应的是什么体了，知道的修改下）
     * @throws IOException
     */
    public static void rewriteFile(File file, String text) throws IOException {
        FileWriter myFileWriter = new FileWriter(file);
        myFileWriter.write(text);
        myFileWriter.close();
    }

    /**
     * 复写文件，新内容会替代旧内容
     *
     * @param path 要写入的文件的路径
     * @param text 要写入的内容
     * @throws IOException
     */
    public static void rewriteFile(String path, String text) throws IOException {
        File myFile = new File(path);
        rewriteFile(myFile, text);
    }

    /**
     * 附加文本内容到指定文件
     *
     * @param file 要附加内容的文件
     * @param text 要附加的文本内容
     * @throws IOException
     */
    public static void appendFile(File file, String text) throws IOException {
        FileWriter myFileWriter = new FileWriter(file);
        myFileWriter.append(text);
        myFileWriter.close();
    }

    /**
     * 附加文本内容到指定文件
     *
     * @param path 要附加内容的文件的路径
     * @param text 要附加的文本内容
     * @throws IOException
     */
    public static void appendFile(String path, String text) throws IOException {
        File myFile = new File(path);
        appendFile(myFile, text);
    }

    /**
     * 删除文件
     *
     * @param path 要删除文件的路径
     * @return 删除成功：true，删除失败：false
     */
    public static boolean deleteFile(String path) {
        File myFile = new File(path);
        return myFile.delete();
    }

    /**
     * 删除指定路径的文件夹和所有子文件（夹）
     *
     * @param path 要删除的文件夹的路径
     * @return 删除成功：true，删除失败：false
     */
    public static boolean deleteDir(String path) {
        deleteSubFiles(path);
        File myDir = new File(path);
        return myDir.delete();
    }

    /**
     * 删除指定路径下的所有的子文件（夹）
     *
     * @param path 指定的的文件夹的路径
     */
    public static void deleteSubFiles(String path) {
        File myFile = new File(path);
        if (!myFile.exists()) {
            return;
        }
        if (!myFile.isDirectory()) {
            return;
        }
        String[] tempList = myFile.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                deleteSubFiles(path + File.separator + tempList[i]);
                deleteDir(path + File.separator + tempList[i]);
            }
        }
    }

    /**
     * 拷贝文件
     *
     * @param source    起始目录
     * @param dest      目标目录
     * @param hProgress 进度的表现，你可以传入一个handler通过handleMessage把进度展示在程序中
     *                  共有三种消息：FileUtils.WHAT_COPY_START、FileUtils.WHAT_COPY_PROGRESS、
     *                  FileUtils.WHAT_COPY_FINISH,每个消息的arg1为已拷贝长度，arg2为总长度，当结
     *                  束时全部重置为0
     * @throws IOException
     */
    public static void copyFile(String source, String dest, Handler hProgress)
            throws IOException {

        File oldFile = new File(source);
        if (oldFile.exists()) {
            InputStream is = new FileInputStream(source);
            FileOutputStream fs = new FileOutputStream(dest);
            int size = is.available();
            if (hProgress != null) {
                Message msg = new Message();
                msg.what = WHAT_COPY_START;
                msg.arg1 = 0;
                msg.arg2 = size;
                hProgress.sendMessage(msg);
            }
            int count = 0;
            int n = 0;

            byte[] buffer = new byte[1444];
            while ((n = is.read(buffer)) != -1) {
                fs.write(buffer, 0, n);
                count += n;
                if (hProgress != null) {
                    Message msg = new Message();
                    msg.what = WHAT_COPY_PROGRESS;
                    msg.arg1 = count;
                    msg.arg2 = size;
                    hProgress.sendMessage(msg);
                }
            }
            is.close();
            fs.close();
            if (hProgress != null) {
                Message msg = new Message();
                msg.what = WHAT_COPY_FINISH;
                msg.arg1 = 0;
                msg.arg2 = 0;
                hProgress.sendMessage(msg);
            }
        }
    }


    public static void copyUriFile(String source, OutputStream dest, Handler hProgress)
            throws IOException {
        File oldFile = new File(source);
        if (oldFile.exists()) {
            InputStream is = new FileInputStream(source);
            OutputStream fs = dest;
            int size = is.available();
            if (hProgress != null) {
                Message msg = new Message();
                msg.what = WHAT_COPY_START;
                msg.arg1 = 0;
                msg.arg2 = size;
                hProgress.sendMessage(msg);
            }
            int count = 0;
            int n = 0;

            byte[] buffer = new byte[1444];
            while ((n = is.read(buffer)) != -1) {
                fs.write(buffer, 0, n);
                count += n;
                if (hProgress != null) {
                    Message msg = new Message();
                    msg.what = WHAT_COPY_PROGRESS;
                    msg.arg1 = count;
                    msg.arg2 = size;
                    hProgress.sendMessage(msg);
                }
            }
            is.close();
            fs.close();
            if (hProgress != null) {
                Message msg = new Message();
                msg.what = WHAT_COPY_FINISH;
                msg.arg1 = 0;
                msg.arg2 = 0;
                hProgress.sendMessage(msg);
            }
        }
    }

    /**
     * 拷贝文件夹，包括其包含的所有文件（夹）和所有子文夹下的文件（夹）如此递归，直到完成
     *
     * @param source 起始文件夹地址
     * @param dest   目标文件夹目录
     * @throws IOException
     */
    public static void copyFolder(String source, String dest)
            throws IOException {
        new File(dest).mkdirs();
        File a = new File(source);
        String[] file = a.list();
        File temp = null;
        for (int i = 0; i < file.length; i++) {
            if (source.endsWith(File.separator)) {
                temp = new File(source + file[i]);
            } else {
                temp = new File(source + File.separator + file[i]);
            }

            if (temp.isFile()) {
                FileInputStream input = new FileInputStream(temp);
                FileOutputStream output = new FileOutputStream(dest
                        + File.separator + (temp.getName()).toString());
                byte[] b = new byte[1024 * 5];
                int len;
                while ((len = input.read(b)) != -1) {
                    output.write(b, 0, len);
                }
                output.flush();
                output.close();
                input.close();
            }
            if (temp.isDirectory()) {
                copyFolder(source + File.separator + file[i], dest
                        + File.separator + file[i]);
            }
        }

    }

    public static Uri generateMediaUriToMovies() {
        Uri audioCollection = null;
//        if (RegexHelper.eq(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                audioCollection = MediaStore.Video.Media.getContentUri(
//                        MediaStore.VOLUME_EXTERNAL_PRIMARY);
//            } else {
//                audioCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                audioCollection = MediaStore.Video.Media.getContentUri(
//                        MediaStore.VOLUME_INTERNAL);
//            } else {
//                audioCollection = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
//            }
//        }
        return audioCollection;
    }


    public static Uri moveFileToMediaStore(Context context, String fileName, String mimeType) {
        Uri dest = generateMediaUriToMovies();
        ContentResolver resolver = context.getApplicationContext()
                .getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
        if (RegexHelper.isNotEmpty(mimeType))
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, mimeType);
        else contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
        return resolver.insert(dest, contentValues);
    }

    /**
     * 移动文件
     *
     * @param source    起始目录
     * @param dest      目标目录
     * @param hProgress 进度的表现，你可以传入一个handler通过handleMessage把进度展示在程序中
     *                  共有三种消息：FileUtils.WHAT_COPY_START、FileUtils.WHAT_COPY_PROGRESS、
     *                  FileUtils.WHAT_COPY_FINISH,每个消息的arg1为已拷贝长度，arg2为总长度，当结
     *                  束时全部重置为0
     * @throws IOException
     */
    public static void moveFile(String source, String dest, Handler hProgress)
            throws IOException {
        copyFile(source, dest, hProgress);
        deleteFile(source);
    }

    public static void moveFile(Context context, String source, Uri dest, Handler hProgress)
            throws IOException {
        OutputStream out = context.getContentResolver().openOutputStream(dest);
        copyUriFile(source, out, hProgress);
        deleteFile(source);
    }

    public static void moveFolder(String source, String dest)
            throws IOException {
        copyFolder(source, dest);
        deleteDir(source);

    }

    /**
     * 逐行读取文本文件
     *
     * @param file 文件
     * @return
     * @throws IOException
     */
    public static List<String> readFile(File file) throws IOException {
        FileReader myFileReader = new FileReader(file);
        BufferedReader myBufferedReader = new BufferedReader(myFileReader);
        String line;
        List<String> fileText = new ArrayList<String>();
        while ((line = myBufferedReader.readLine()) != null) {
            fileText.add(line);
        }
        myBufferedReader.close();
        myFileReader.close();
        return fileText;
    }

    public static String readFileAsString(File file) throws IOException {
        return inputStream2String(new FileInputStream(file), Charset.forName("UTF-8").name());
    }


    /**
     * 将字符串数组转化为一个字符串，原始数组中每个字符串单独占一行
     *
     * @param list
     * @return
     */
    public static String toString(List<String> list) {
        String ret = "";
        for (String s : list) {
            ret += s + "\n";
        }
        return ret;
    }

    /**
     * 逐行读取文本文件
     *
     * @param path 文件地址
     * @return
     * @throws IOException
     */
    public static List<String> readFile(String path) throws IOException {
        File myFile = new File(path);
        return readFile(myFile);
    }

    /**
     * 获取目录大小
     *
     * @param path
     * @return
     */
    public static long getDirSize(String path) {
        return getDirSize(new File(path));
    }

    /**
     * 获取目录大小（包含所有子文件（夹））
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file);
            }
        }
        return dirSize;
    }

    /**
     * 判断文件是否存在
     *
     * @param filepath
     * @return boolean
     */
    public static boolean checkFileExist(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static String inputStream2String(InputStream inputStream,
                                            String encoding) {

        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();

        try {
            if (encoding != null && !encoding.trim().equals("")) {
                reader = new InputStreamReader(inputStream, encoding);
            } else {
                reader = new InputStreamReader(inputStream);
            }
            // 将输入流写入输出流
            char[] buffer = new char[4096];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return writer.toString();
    }

    /**
     * 从安装包assets中复制文件到指定路径
     *
     * @param context
     * @param fileName 待复制的文件名
     * @param destPath 目标路径
     * @return
     */
    public static File copyFromAsset(Context context, String fileName, String destPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(fileName);
            File file = new File(destPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
            is.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {

                }
                fos = null;
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                }
                is = null;
            }
            return null;
        }

    }


    /**
     * 得到文件名字（不包含扩展名）
     * <p>
     * 例：/sdcard/test/abc.zxy/ -> abc
     *
     * @param filePath
     * @return
     */
    public static String getFileNameWithoutExt(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extensionPosition = filePath.lastIndexOf(".");
        int filePosition = filePath.lastIndexOf(File.separator);
        if (filePosition == -1) {
            return (extensionPosition == -1 ? filePath : filePath.substring(0, extensionPosition));
        }

        if (extensionPosition == -1) {
            return filePath.substring(filePosition + 1);
        }

        return (filePosition < extensionPosition ? filePath.substring(filePosition + 1, extensionPosition) : filePath.substring(filePosition + 1));
    }


    /**
     * 根据输入的文件路径，得到扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileExt(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extPosi = filePath.lastIndexOf(".");
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extPosi == -1) {
            return "";
        }
        return (filePosi >= extPosi) ? "" : filePath.substring(extPosi + 1);
    }


    public static boolean writeFile(Bitmap bitmap, File file, int quality) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            return bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
