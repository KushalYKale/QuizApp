package com.kushal.question_service.service;


import com.kushal.question_service.dao.QuestionDao;
import com.kushal.question_service.model.QuestionWrapper;
import com.kushal.question_service.model.Questions;
import com.kushal.question_service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<List<Questions>> getAllQuestions() {
        try {
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<List<Questions>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(questionDao.findByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<String> addQuestion(Questions questions) {
        questionDao.save(questions);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {

        List<Integer> questions = questionDao.findRandomQuestionByCategory(categoryName, numQuestions);
        return new ResponseEntity<>(questions, HttpStatus.OK);


    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        List<QuestionWrapper> questionWrappers = new ArrayList<>();
        List<Questions> questions = new ArrayList<>();

        for (Integer id : questionIds) {
            Optional<Questions> optionalQuestion = questionDao.findById(id);
            if (optionalQuestion.isPresent()) {
                questions.add(optionalQuestion.get());
            }
        }

        for (Questions questions1 : questions) {
            QuestionWrapper wrapper = new QuestionWrapper();
            wrapper.setId(questions1.getId());
            wrapper.setQuestion(questions1.getQuestion());
            wrapper.setOption1(questions1.getOption1());
            wrapper.setOption2(questions1.getOption2());
            wrapper.setOption3(questions1.getOption3());
            wrapper.setOption4(questions1.getOption4());
            questionWrappers.add(wrapper);
        }

        return new ResponseEntity<>(questionWrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {

        int right = 0;

        for (Response response : responses) {
            Questions questions = questionDao.findById(response.getId()).get();
            if (response.getResponse().equals(questions.getCorrectAnswer()))
                right++;
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
