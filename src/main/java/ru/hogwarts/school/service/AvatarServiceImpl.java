package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);


    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    public AvatarServiceImpl(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    @Override

    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {

        logger.info("Was invoked method for upload avatar");

        try {
            Student student = studentRepository.getById(studentId);
            String originalFilename = avatarFile.getOriginalFilename();

            if (originalFilename == null || originalFilename.isEmpty()) {
                logger.error("Original filename is empty for studentId: {}", studentId);
                throw new IllegalArgumentException("Original filename must not be empty.");
            }

            Path filePath = Path.of(avatarsDir, student + "." + getExtensions(originalFilename));
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);

            try (
                    InputStream is = avatarFile.getInputStream();
                    OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                    BufferedInputStream bis = new BufferedInputStream(is, 1024);
                    BufferedOutputStream bos = new BufferedOutputStream(os, 1024);

            ) {

                bis.transferTo(bos);

            } catch (IOException e) {

                logger.error("Error during file transfer for studentId: {}", studentId, e);
                throw e;

            }

            Avatar avatar = findAvatar(studentId);
            avatar.setStudent(student);
            avatar.setFilePath(filePath.toString());
            avatar.setFileSize(avatarFile.getSize());
            avatar.setMediaType(avatarFile.getContentType());
            avatar.setData(generateDataForDB(filePath));
            avatarRepository.save(avatar);

        } catch (EntityNotFoundException e) {

            logger.error("Student with id {} not found.", studentId);
            throw e;

        } catch (IOException e) {

            logger.error("IO Exception occurred while uploading avatar for student {}: ", studentId, e);
            throw e;

        } catch (Exception e) {

            logger.error("Unexpected error occurred while saving avatar for student {}: ", studentId, e);
            throw new RuntimeException("Failed to save avatar", e);

        }

    }

    public Avatar findAvatar(Long studentId) {
        logger.info("Was invoked method for find avatar");
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    private String getExtensions(String Filename) {
        logger.info("Was invoked method for get extensions of avatar" + Filename);
        return Filename.substring(Filename.lastIndexOf(".") + 1);
    }

    private byte[] generateDataForDB(Path filePath) throws IOException {
        logger.info("Was invoked method for generate data for DB");
        try (
                InputStream is = Files.newInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                logger.error("Could not read image from file: {}", filePath);
                throw new IOException("Could not read image from file: " + filePath);
            }

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics2D = preview.createGraphics();
            graphics2D.drawImage(image, 0, 0, 100, height, null);
            graphics2D.dispose();

            String fileExtension = getExtensions(filePath.getFileName().toString());
            if (fileExtension == null || fileExtension.isEmpty()) {
                logger.warn("File extension not found for file: {}", filePath);
            }

            return baos.toByteArray();

        } catch (IOException e) {
            logger.error("IO Exception occurred while generating data for DB from file: {}", filePath, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while generating data for DB from file: {}", filePath, e);
            throw new IOException("Error generating data for DB: " + e.getMessage(), e);
        }
    }

    public List<Avatar> getAllAvatars(Integer pageNumber, Integer pageSize) {
        logger.info("Was invoked method for get all avatars");
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}
