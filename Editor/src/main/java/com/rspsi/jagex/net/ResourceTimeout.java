package com.rspsi.jagex.net;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ResourceTimeout {

	private final ResourceRequest request;
	private Optional<Throwable> reason;

}
