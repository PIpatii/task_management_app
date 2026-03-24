package management.application.service;

import java.util.Arrays;
import java.util.Optional;
import management.application.dto.label.CreateLabelRequestDto;
import management.application.dto.label.LabelDto;
import management.application.dto.label.UpdateLabelRequestDto;
import management.application.mapper.LabelMapper;
import management.application.model.Label;
import management.application.repository.LabelRepository;
import management.application.service.impl.LabelServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LabelServiceTest {
    @InjectMocks
    private LabelServiceImpl labelService;
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private LabelMapper labelMapper;

    @Test
    @DisplayName("get all labels")
    public void getAllLabels_success() {
        Label firstLabel = new Label();
        firstLabel.setId(1L);
        firstLabel.setName("First Label");
        firstLabel.setColor("red");

        Label secondLabel = new Label();
        secondLabel.setId(2L);
        secondLabel.setName("Second Label");
        secondLabel.setColor("green");

        int expectedSize = 2;

        when(labelRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(firstLabel, secondLabel)));
        Page<LabelDto> actual = labelService.getAllLabels(PageRequest.of(0, 10));
        assertEquals(expectedSize, actual.getContent().size());
    }

    @Test
    @DisplayName("create a label")
    public void createLabel_success() {
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto();
        requestDto.setName("First Label");
        requestDto.setColor("red");

        Label label = new Label();
        label.setId(1L);
        label.setName(requestDto.getName());
        label.setColor(requestDto.getColor());

        LabelDto labelDto = new LabelDto();
        labelDto.setId(label.getId());
        labelDto.setName(label.getName());
        labelDto.setColor(label.getColor());

        when(labelMapper.toEntity(requestDto)).thenReturn(label);
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(labelDto);

        LabelDto actual = labelService.createLabel(requestDto);

        assertEquals(labelDto.getId(), actual.getId());
        assertEquals(labelDto.getName(), actual.getName());
        assertEquals(labelDto.getColor(), actual.getColor());
    }

    @Test
    @DisplayName("update a label by id")
    public void updateLabel_success() {
        UpdateLabelRequestDto requestDto = new UpdateLabelRequestDto();
        requestDto.setName("First Label");
        requestDto.setColor("red");

        Label label = new Label();
        label.setId(1L);
        label.setName("updated First Label");
        label.setColor("green");

        LabelDto expected = new LabelDto();
        expected.setId(label.getId());
        expected.setName(label.getName());
        expected.setColor(label.getColor());

        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        doAnswer(invocation -> {
            UpdateLabelRequestDto dto = invocation.getArgument(0);
            Label updatedLabel = invocation.getArgument(1);
            updatedLabel.setName(dto.getName());
            updatedLabel.setColor(dto.getColor());

            return null;
        }).when(labelMapper).updateLabel(any(UpdateLabelRequestDto.class), any(Label.class));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(expected);

        LabelDto actual = labelService.updateLabel(requestDto, 1L);

        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getName(), expected.getName());
        assertEquals(actual.getColor(), expected.getColor());
    }

    @Test
    @DisplayName("delete a label by id")
    public void deleteLabel_success() {
        Long labelId = 1L;

        doNothing().when(labelRepository).deleteById(labelId);
        labelService.deleteLabel(labelId);

        verify(labelRepository, times(1)).deleteById(labelId);
    }
}
