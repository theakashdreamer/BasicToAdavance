import 'package:flutter/material.dart';
class DynamicQuestionsPage extends StatefulWidget {
  @override
  _DynamicQuestionsPageState createState() => _DynamicQuestionsPageState();
}

class _DynamicQuestionsPageState extends State<DynamicQuestionsPage> {
  List<Question> questions = [
    Question(
      type: QuestionType.radio,
      question: 'How much does an average barber make in San Francisco?',
      options: ['\$40,000', '\$55,000', '\$65,000', '\$75,000'],
    ),
    Question(
      type: QuestionType.checkbox,
      question: 'Select your hobbies:',
      options: ['Reading', 'Traveling', 'Gaming'],
    ),
    Question(
      type: QuestionType.text,
      question: 'Tell us about yourself:',
    ),
  ];

  Map<int, dynamic> answers = {};
  int currentQuestionIndex = 0;

  void handleSubmit() {
    print('User submitted:');
    for (int i = 0; i < questions.length; i++) {
      print('Q${i + 1}: ${questions[i].question}');
      print('Answer: ${answers[i] ?? "No answer"}\n');
    }

    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: Text('Form Submitted'),
        content: Text('Thank you! Your answers have been submitted.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('OK'),
          )
        ],
      ),
    );
  }

  void goToNextQuestion() {
    if (currentQuestionIndex < questions.length - 1) {
      setState(() {
        currentQuestionIndex++;
      });
    }
  }

  void goToPreviousQuestion() {
    if (currentQuestionIndex > 0) {
      setState(() {
        currentQuestionIndex--;
      });
    }
  }

  Widget buildCurrentQuestion(int index) {
    final q = questions[index];

    return Card(
      margin: EdgeInsets.all(16),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      elevation: 3,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(q.question,
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
            SizedBox(height: 12),

            if (q.type == QuestionType.radio)
              ...q.options!.map((option) => Padding(
                padding: const EdgeInsets.symmetric(vertical: 4.0),
                child: GestureDetector(
                  onTap: () {
                    setState(() {
                      answers[index] = option;
                    });
                  },
                  child: Container(
                    padding:
                    EdgeInsets.symmetric(horizontal: 12, vertical: 12),
                    decoration: BoxDecoration(
                      color: answers[index] == option
                          ? Colors.blue[100]
                          : Colors.grey[100],
                      borderRadius: BorderRadius.circular(10),
                      border: Border.all(
                        color: answers[index] == option
                            ? Colors.blue
                            : Colors.grey[300]!,
                        width: 1.5,
                      ),
                    ),
                    child: Row(
                      children: [
                        Icon(
                          answers[index] == option
                              ? Icons.radio_button_checked
                              : Icons.radio_button_off,
                          color: answers[index] == option
                              ? Colors.blue
                              : Colors.grey,
                        ),
                        SizedBox(width: 10),
                        Text(option),
                      ],
                    ),
                  ),
                ),
              )),

            if (q.type == QuestionType.checkbox)
              ...q.options!.map((option) {
                List<String> selected =
                List<String>.from(answers[index] ?? []);
                bool isChecked = selected.contains(option);

                return Padding(
                  padding: const EdgeInsets.symmetric(vertical: 4.0),
                  child: GestureDetector(
                    onTap: () {
                      setState(() {
                        if (isChecked) {
                          selected.remove(option);
                        } else {
                          selected.add(option);
                        }
                        answers[index] = selected;
                      });
                    },
                    child: Container(
                      padding:
                      EdgeInsets.symmetric(horizontal: 12, vertical: 12),
                      decoration: BoxDecoration(
                        color: isChecked ? Colors.blue[100] : Colors.grey[100],
                        borderRadius: BorderRadius.circular(10),
                        border: Border.all(
                          color: isChecked ? Colors.blue : Colors.grey[300]!,
                          width: 1.5,
                        ),
                      ),
                      child: Row(
                        children: [
                          Icon(
                            isChecked
                                ? Icons.check_box
                                : Icons.check_box_outline_blank,
                            color: isChecked ? Colors.blue : Colors.grey,
                          ),
                          SizedBox(width: 10),
                          Text(option),
                        ],
                      ),
                    ),
                  ),
                );
              }),

            if (q.type == QuestionType.text)
              TextField(
                onChanged: (val) {
                  answers[index] = val;
                },
                decoration: InputDecoration(
                  hintText: 'Enter your answer',
                  contentPadding:
                  EdgeInsets.symmetric(horizontal: 12, vertical: 12),
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12)),
                ),
              ),

            SizedBox(height: 20),


          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Dynamic Questions'),
        centerTitle: true,
      ),
      body: Column(
        children: [
          buildCurrentQuestion(currentQuestionIndex),
          // Buttons
          Container(
            margin: EdgeInsets.symmetric(horizontal: 20),
            child: Row(

              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                if (currentQuestionIndex > 0)
                  OutlinedButton(
                    style: OutlinedButton.styleFrom(
                      side: BorderSide(color: Colors.blue),
                      foregroundColor: Colors.blue,
                      padding: EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                    onPressed: goToPreviousQuestion,
                    child: Text(
                      'Previous',
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
                    ),
                  ),

                OutlinedButton(
                  style: OutlinedButton.styleFrom(
                    side: BorderSide(color: Colors.blue),
                    foregroundColor: Colors.blue,
                    padding: EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  onPressed: goToNextQuestion,
                  child: Text('Skip'),
                ),
                if (currentQuestionIndex < questions.length - 1)
                  OutlinedButton(
                    style: OutlinedButton.styleFrom(
                      side: BorderSide(color: Colors.blue),
                      foregroundColor: Colors.blue,
                      padding: EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                    onPressed: goToNextQuestion,
                    child: Text('Next'),
                  ),
                if (currentQuestionIndex == questions.length - 1)
                  ElevatedButton(
                    onPressed: handleSubmit,
                    child: Text('Submit'),
                  ),
              ],
            ),
          )
        ],
      ),
    );
  }
}

enum QuestionType { radio, checkbox, text }

class Question {
  final QuestionType type;
  final String question;
  final List<String>? options;

  Question({
    required this.type,
    required this.question,
    this.options,
  });
}
