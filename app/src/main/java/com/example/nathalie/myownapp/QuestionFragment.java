package com.example.nathalie.myownapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends android.support.v4.app.ListFragment {

    RequestQueue requestQueue;
    String url = "https://opentdb.com/api.php?amount=12&type=multiple";
    ArrayList<Question> questionList = new ArrayList<Question>();
    int score = 0;
    int oldScore = 0;
    int questionCounter = 0;
    TextView question_tv, score_tv;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getJSON();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_question, container, false);

        question_tv = (TextView) view.findViewById(R.id.question_tv);
        score_tv = (TextView) view.findViewById(R.id.score_tv);

        // Inflate the layout for this fragment
        return view;
    }

    // Show question and answers in lay-out
    public void showQuestionAndAnswers() {
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item,
                questionList.get(questionCounter).getAnswers());
        this.setListAdapter(theAdapter);

        question_tv.setText(Html.fromHtml(String.valueOf(questionList.get(questionCounter).getQuestion())));
    }

    @Override
    public void onListItemClick(ListView listview, View view, int position, long id) {
        super.onListItemClick(listview, view, position, id);
        Log.d("hallo_counter", String.valueOf(questionCounter));
        // Make sure the list does not go out of bound
        if (questionCounter < 11) {
            // Initialize bundle to send to next fragment
            QuestionFragment questionFragment = new QuestionFragment();
            String clickedAnswer = listview.getItemAtPosition(position).toString();
            Bundle args = new Bundle();
            args.putString("answers", clickedAnswer);
            questionFragment.setArguments(args);

            // Check if user clicked correct answer
            boolean checkAnswer = checkAnswer(clickedAnswer,
                    String.valueOf(questionList.get(questionCounter).getCorrectAnswer()));
            Toast.makeText(getActivity().getApplicationContext(), "Question " +
                    checkAnswer, Toast.LENGTH_SHORT).show();
            score_tv.setText(String.valueOf(score));
            questionCounter += 1;

            // Fill lay-oyt with the API details
            showQuestionAndAnswers();
        // Let user decide if he/she want to add score to leaderboard
        } else {
            if(checkIfHighscore()) {
                showAlert("Congratulations! You improved your score. Want to set " + score +
                        " as highscore for the leaderboard?",
                        "Yes", "No, go back to mainpage",checkIfHighscore());
            } else {
                showAlert("You didn't improve your highscore of " + oldScore,  "Show leaderboard","Go back to mainpage", checkIfHighscore());
            }
        }
    }

    public void getJSON() {

        // Initialize a new RequestQueue instance
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // Process the JSON
                        try {
                            // Get the JSON array
                            JSONArray array = response.getJSONArray("results");

                            // Get the API details in the received JSON array
                            getAPIDetails(array);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JSON ERROR", "Error when requesting a response from JSON");
                    }
                }
        );

        // Add JsonObjectRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);

    }

    public void getAPIDetails(JSONArray array) {
        // Fill list with elements from JSON
        try {
            for (int i = 0; i < array.length(); i++) {
                // Get the JSON object in the JSON array
                JSONObject object = array.getJSONObject(i);

                // Retrieve values from the JSON
                String question = array.getJSONObject(i).getString("question");
                JSONArray incorrectAnswersJSON = object.getJSONArray("incorrect_answers");
                String correctAnswer = array.getJSONObject(i).getString("correct_answer");
                String category = array.getJSONObject(i).getString("category");

                // Fill the answerlist with answers
                List<String> answerList = new ArrayList<String>();
                answerList.add(correctAnswer);
                for (int j = 0; j < (incorrectAnswersJSON.length()); j++) {
                    answerList.add(String.valueOf(incorrectAnswersJSON.get(j)));
                }
                Collections.shuffle(answerList);

                // Add question details instance to question list
                questionList.add(new Question(question, answerList, correctAnswer, category));
            }

            // Show categories in list view
            showQuestionAndAnswers();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Check is answer is correct and keeps track of score
    public boolean checkAnswer(String chosenAnswer, String correctAnswer) {
        Log.d("hallo_chosen, correct", chosenAnswer + ", " + correctAnswer);
        if (chosenAnswer == correctAnswer) {
            score += 10;
            return true;
        } else {
            return false;
        }
    }

    public void showAlert (String title, String positiveButton, String negativeButton, final boolean addHighscore) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Done!");
        adb.setMessage(title);
        adb.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(addHighscore) {
                    changeScoreInDB();

                    // Redirects user to the leaderboard
                    Intent intent = new Intent(getActivity(), LeaderbordActivity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            } });
        adb.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Redirects user back to main page
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                dialog.dismiss();
            } });
        adb.show();
    }
    public void changeScoreInDB (){
        final DatabaseReference updateScore = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid())).child("score");
        updateScore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Change score to new score in database
                updateScore.setValue(score);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void getScoreFromDB (){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User aUser = dataSnapshot.child(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid())).getValue(User.class);
                oldScore = aUser.score;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    public boolean checkIfHighscore() {
        getScoreFromDB();
        Log.d("hallo: old, new", String.valueOf(oldScore + ", " + score));
        if (oldScore < score) {
            return true;

        } else {
            return false;
        }
    }
}
