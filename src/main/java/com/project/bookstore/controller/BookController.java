package com.project.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstore.common.Util;
import com.project.bookstore.common.WConstants;
import com.project.bookstore.model.BookEntity;
import com.project.bookstore.model.ReviewEntity;
import com.project.bookstore.model.ReviewInputData;
import com.project.bookstore.service.BookService;
import com.project.bookstore.service.ReviewService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    Logger log = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;
    @Autowired
    private ReviewService reviewService;

    /*
     * @return list of next 10 books based on pageNo offset
     * @throws Exception
     */

    @GetMapping("/getAllBooks")
    public List<BookEntity> getAllBooks(@RequestParam(required = false, defaultValue = "1") Integer pageno) {
        try {
            return bookService.getAllBooks(pageno);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * @return List of Category
     */

    @GetMapping("/findByCategory")
    public List<BookEntity> findBooksByCategory(@RequestParam String category, @RequestParam(required = false, defaultValue = "1") Integer pageno) {
        try {
            return bookService.getBooksByCategory(category,pageno);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /*
     * Controller to get all the categories
     * @return List of all Category
     * */

    @GetMapping("/getAllCategory")
    public List<String> getAllCategory() {
        try {
            return bookService.getAllCategory();
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @param bid - book id
     * @return book entity as a JSON String (with indentation), otherwise error with status code and message
     * @apiNote for both client and partners
     */
    @GetMapping("/getProductInfo")
    public String getBookInfo(@RequestParam(name = "bid") int bid){
        log.debug(String.format("Entered getProductInfo for bid: %s", bid));
        ObjectMapper mapper = new ObjectMapper();
        try{
            JSONObject json = new JSONObject();
            BookEntity book = bookService.getBookInfo(bid);
            if(book == null){
                json.put("status", WConstants.RESPONSE_FAIL);
                json.put("message", "Please enter a valid book/product ID.");
                return json.toString(4);
            }
            return mapper.writeValueAsString(book);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Util.getJsonResponse(WConstants.RESULT_UNKNOWN_ERROR, null);
        }
    }

    /**
     * @param title: book title/seach query
     * @return list of all books containing that title word
     */
    @GetMapping("/searchByTitle")
    public List<BookEntity> searchBooksByTitle(@RequestParam(name = "title") String title){
        log.debug(String.format("Entered searchBooksByTitle for title: %s", title));
        try{
            return bookService.searchBooksByTitle(title);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /* get Reviews for a book */
    @GetMapping("/getReviews")
    public List<ReviewEntity> getReviewsForBook(@RequestParam(name = "bid") int bid){
        try{
            return reviewService.getReviewsForBook(bid);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /* Add a review for a book */
    @PostMapping("/addReview")
    public String addReview(@RequestBody String data){
        log.debug(String.format("Entered addReview for data: %s", data));

        try{
            ObjectMapper mapper = new ObjectMapper();
            ReviewInputData inputData = mapper.readValue(data, ReviewInputData.class);
            return reviewService.addReview(inputData);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return Util.getJsonResponse(WConstants.RESULT_UNKNOWN_ERROR, null);
        }
    }


}
