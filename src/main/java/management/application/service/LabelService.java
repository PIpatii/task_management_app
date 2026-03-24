package management.application.service;

import management.application.dto.label.CreateLabelRequestDto;
import management.application.dto.label.LabelDto;
import management.application.dto.label.UpdateLabelRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    Page<LabelDto> getAllLabels(Pageable pageable);

    LabelDto createLabel(CreateLabelRequestDto requestDto);

    LabelDto updateLabel(UpdateLabelRequestDto requestDto, Long id);

    void deleteLabel(Long id);
}
