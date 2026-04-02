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
import static management.application.helper.TestDataHelper.createCreateLabelRequestDto;
import static management.application.helper.TestDataHelper.createLabel;
import static management.application.helper.TestDataHelper.createLabelDto;
import static management.application.helper.TestDataHelper.createUpdateLabelRequestDto;
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
        Label firstLabel = createLabel(1L, "First label", "red");

        Label secondLabel =createLabel(2L, "Second label", "green");

        int expectedSize = 2;

        when(labelRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(firstLabel, secondLabel)));
        Page<LabelDto> actual = labelService.getAllLabels(PageRequest.of(0, 10));
        assertEquals(expectedSize, actual.getContent().size());
    }

    @Test
    @DisplayName("create a label")
    public void createLabel_success() {
        CreateLabelRequestDto requestDto = createCreateLabelRequestDto("label", "red");

        Label label = createLabel(1L, requestDto.getName(), requestDto.getColor());

        LabelDto expected = createLabelDto(label.getId(), label.getName(), label.getColor());

        when(labelMapper.toEntity(requestDto)).thenReturn(label);
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(expected);

        LabelDto actual = labelService.createLabel(requestDto);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getColor(), actual.getColor());
    }

    @Test
    @DisplayName("update a label by id")
    public void updateLabel_success() {
        UpdateLabelRequestDto requestDto = createUpdateLabelRequestDto("label", "red");

        Label label = createLabel(1L, requestDto.getName(), requestDto.getColor());

        LabelDto expected = createLabelDto(label.getId(), label.getName(), label.getColor());

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
