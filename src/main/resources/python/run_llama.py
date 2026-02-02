import sys
from llama_cpp import Llama

# Load model ONCE
model = Llama(
    model_path="/Users/shashank/Documents/ideaSpace/llama.cpp/models/ggml-alpaca-7b-q4.bin",
    n_ctx=2048,
)

print("READY", flush=True)  # optional handshake

while True:
    try:
        line = sys.stdin.readline()
        if not line:
            continue  # do NOT exit

        prompt = line.strip()
        if not prompt:
            continue

        # Alpaca-style prompt (important)
        full_prompt = f"""### Instruction:
{prompt}

### Response:
"""

        result = model(
            full_prompt,
            max_tokens=128,
            stop=["###"],
        )

        text = result["choices"][0]["text"].strip()

        # CRITICAL: single-line response
        print(text.replace("\n", " "), flush=True)

    except Exception as e:
        print(f"ERROR: {e}", file=sys.stderr, flush=True)
