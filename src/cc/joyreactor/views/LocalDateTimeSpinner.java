package cc.joyreactor.views;

import com.alee.extended.layout.HorizontalFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class LocalDateTimeSpinner extends JPanel {

    private LocalDateTime dt = LocalDateTime.now();

    private JLabel dayLabel;
    private JLabel monthLabel;
    private JLabel yearLabel;
    private JLabel hourLabel;
    private JLabel minuteLabel;
    private JLabel secondLabel;

    public LocalDateTimeSpinner() {
        super(new HorizontalFlowLayout(0, false));
        initUI();
    }

    private void initUI() {
        dayLabel = new JLabel();
        setStyle(dayLabel);
        add(dayLabel);

        monthLabel = new JLabel();
        setStyle(monthLabel);
        add(monthLabel);

        yearLabel = new JLabel();
        setStyle(yearLabel);
        add(yearLabel);

        hourLabel = new JLabel();
        setStyle(hourLabel);
        add(hourLabel);

        JLabel timeDivLabel = new JLabel(": ");
        setStyle(timeDivLabel);
        add(timeDivLabel);

        minuteLabel = new JLabel();
        setStyle(minuteLabel);
        add(minuteLabel);


        JLabel timeDivLabel2 = new JLabel(": ");
        setStyle(timeDivLabel2);
        add(timeDivLabel2);

        secondLabel = new JLabel();
        setStyle(secondLabel);
        add(secondLabel);
    }

    private void setStyle(JLabel label) {
        label.setFont(label.getFont().deriveFont(Font.ITALIC, 16.0f));
        //label.setBorder(UIManager.getBorder("ScrollPane.border"));
    }

    private void update() {
        dayLabel.setText(" " + dt.getDayOfMonth() + " ");
        monthLabel.setText(" " + dt.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " ");
        yearLabel.setText(" " + dt.getYear() + " ");
        hourLabel.setText(" " + dt.getHour() + " ");
        minuteLabel.setText(" " + dt.getMinute() + " ");
        secondLabel.setText(" " + dt.getSecond() + " ");
    }

    public LocalDateTime getDt() {
        return LocalDateTime.of(dt.toLocalDate(), dt.toLocalTime());
    }

    public void setDt(LocalDateTime dt) {
        this.dt = LocalDateTime.of(dt.toLocalDate(), dt.toLocalTime());
        update();
    }
}
