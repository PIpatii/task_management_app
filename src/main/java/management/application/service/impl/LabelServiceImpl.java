package management.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import management.application.dto.label.CreateLabelRequestDto;
import management.application.dto.label.LabelDto;
import management.application.dto.label.UpdateLabelRequestDto;
import management.application.mapper.LabelMapper;
import management.application.model.Label;
import management.application.repository.LabelRepository;
import management.application.service.LabelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public Page<LabelDto> getAllLabels(Pageable pageable) {
        return labelRepository.findAll(pageable).map(labelMapper::toDto);
    }

    @Override
    public LabelDto createLabel(CreateLabelRequestDto requestDto) {
        return labelMapper.toDto(labelRepository.save(labelMapper.toEntity(requestDto)));
    }

    @Override
    public LabelDto updateLabel(UpdateLabelRequestDto requestDto, Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));
        labelMapper.updateLabel(requestDto, label);

        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public void deleteLabel(Long id) {
        labelRepository.deleteById(id);
    }
}
