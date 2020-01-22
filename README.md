# tuition-attendance-taker
Simple attendance taking app for tuition centres.

- Export and import as CSV file, can serve as a backup
- Automatically detect CSV to import by reading the header
- A student consists of id, timestamp, name, nickname, school grade, language and phone number
- Each attendance consists of id, timestamp, name and duration
- Attendance is linked to the student by the name
- Editing the student's name will also update all of attendance with the said name, but it does not work the other way round
- Student language options are indonesian and english
- Edit attendance timestamp for calendarview is not fully done yet, currently a slight typo will cause the whole application to become unusable
- Might add a refresh button for the attendance and student list in the near future
- Default activity is MainActivity, login screen template is included though (just change the manifest)
