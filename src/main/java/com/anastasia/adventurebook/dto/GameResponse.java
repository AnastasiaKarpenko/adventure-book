package com.anastasia.adventurebook.dto;

import com.anastasia.adventurebook.model.Game;
import com.anastasia.adventurebook.model.GameStatus;
import com.anastasia.adventurebook.model.Section;
import com.anastasia.adventurebook.model.SectionOption;

import java.util.List;
import java.util.stream.IntStream;

public record GameResponse(
        Long gameId,
        Long bookId,
        GameStatus status,
        int health,
        String consequence,
        SectionView section
) {

    public static GameResponse from(Game game, String consequence) {
        boolean canChoose = game.getStatus() == GameStatus.IN_PROGRESS;
        return new GameResponse(game.getId(), game.getBook().getId(), game.getStatus(),
                game.getHealth(), consequence, SectionView.from(game.getCurrentSection(), canChoose));
    }

    public record SectionView(int number, String text, List<OptionView> options) {

        static SectionView from(Section section, boolean showOptions) {
            List<SectionOption> options = section.getOptions();
            List<OptionView> views = showOptions
                    ? IntStream.range(0, options.size())
                    .mapToObj(i -> new OptionView(i, options.get(i).getDescription()))
                    .toList()
                    : List.of();
            return new SectionView(section.getNumber(), section.getText(), views);
        }
    }

    public record OptionView(int index, String description) {
    }
}