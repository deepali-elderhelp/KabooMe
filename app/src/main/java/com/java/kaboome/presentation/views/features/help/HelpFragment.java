package com.java.kaboome.presentation.views.features.help;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.java.kaboome.R;
import com.java.kaboome.presentation.views.features.help.viewmodel.HelpViewModel;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HelpFragment extends Fragment {

    private View rootView;
    private EditText subject;
    private EditText messageText;
    private HelpViewModel helpViewModel;
    private NavController navController;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helpViewModel = ViewModelProviders.of(this).get(HelpViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        subject = rootView.findViewById(R.id.fr_feedback_title);
        messageText = rootView.findViewById(R.id.fr_feedback_message_text);
        Button sendFeedbackButton = rootView.findViewById(R.id.send_feedback_button);
        sendFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                SpannableString subjectText = SpannableString.valueOf(subject.getText().toString());
//                String htmlEncodedSubject = Html.toHtml(subjectText);
//                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                RequestBody body = RequestBody.create(JSON, messageText.getText().toString());

//                String messageBody = new Gson().toJson(messageText.getText().toString()).replace("\"\"", "\"");
//                String messageBody = new Gson().toJson(messageText.getText().toString(), String.class);
//                String messageBody = "sdjsdijsdjsd sdkfjdskfj\nksdfksdfksj\njsdfsjd";

//                SpannableString contentText = SpannableString.valueOf(messageText.getText());
//                String htmlEncodedBody = Html.toHtml(contentText);
//                String messageBody = new Gson().toJson(htmlEncodedBody);

//                helpViewModel.sendHelpFeedbackMessage(htmlEncodedSubject,htmlEncodedBody, true );
//                helpViewModel.sendHelpFeedbackMessage(subject.getText().toString(),messageText.getText().toString(), true );
                helpViewModel.sendHelpFeedbackMessage(subject.getText().toString(),messageText.getText().toString(), true );
                //now go back to GLF
                navController.popBackStack();




            }
        });
        navController = NavHostFragment.findNavController(HelpFragment.this);
        return rootView;
    }


}