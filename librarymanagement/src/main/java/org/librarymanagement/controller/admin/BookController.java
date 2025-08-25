package org.librarymanagement.controller.admin;

import jakarta.servlet.http.HttpServletResponse;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.BookListDto;
import org.librarymanagement.service.BookService;
import org.librarymanagement.utils.ExcelValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller("adminBookController")
@RequestMapping(ApiEndpoints.ADMIN_BOOK)
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String showBooklist(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            Model model
    ) {
        model.addAttribute("author", (author == null || author.isBlank()) ? null : author);
        model.addAttribute("publisher", (publisher == null || publisher.isBlank()) ? null : publisher);
        model.addAttribute("genre", (genre == null || genre.isBlank()) ? null : genre);

        Pageable pageable = PageRequest.of(page, 20);
        Page<BookListDto> bookPage = bookService.findAllBooksWithFilter(author, publisher, genre, pageable);
        int totalPages = bookPage.getTotalPages();

        // Danh sách rỗng
        if (totalPages == 0) {
            model.addAttribute("books", List.of());
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 1);
            model.addAttribute("size", size);
        }
        // Page vượt quá tổng số trang, chuyển về trang có số trang lớn nhất
        else if (page >= totalPages) {
            return "redirect:/admin/books?page=" + (totalPages - 1)
                    + (author != null ? "&author=" + author : "")
                    + (publisher != null ? "&publisher=" + publisher : "")
                    + (genre != null ? "&genre=" + genre : "")
                    + "&size=" + size;
        }
        else {
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", page);
            model.addAttribute("size", size);
        }
        return "admin/books/index";
    }

    @GetMapping("/{id}/edit")
    public String editBook() {
        return "admin/books/edit";
    }


    @GetMapping("/{id}")
    public String showBookdetail() {
        return "admin/books/detail";
    }

    @PostMapping("/import")
    public String importBooks(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            // Validate trước khi import
            List<String> errors = ExcelValidator.validateExcelFile(file.getInputStream());
            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Import thất bại, file không hợp lệ!");
                redirectAttributes.addFlashAttribute("errors", errors); // gửi danh sách lỗi sang view
                redirectAttributes.addFlashAttribute("alertType", "danger");
                return "redirect:" + ApiEndpoints.ADMIN_BOOK;
            }

            // Nếu file hợp lệ thì mới gọi service để import
            bookService.importBooksFromExcel(file);
            redirectAttributes.addFlashAttribute("message", "Import thành công!");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Import thất bại: " + e.toString());
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }
        return "redirect:" + ApiEndpoints.ADMIN_BOOK;
    }

    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("static/public/files/Template.xlsx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=book_template.xlsx");
        StreamUtils.copy(resource.getInputStream(), response.getOutputStream());
    }

}
