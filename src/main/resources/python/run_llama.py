import sys
from llama_cpp import Llama

model = Llama(
    model_path="/Users/shashank/Documents/ideaSpace/llama.cpp/models/ggml-alpaca-7b-q4.bin",
    n_ctx=2048,
)

# IMPORTANT: do not print anything except responses
while True:
    line = sys.stdin.readline()
    if not line:
        continue

    prompt = line.strip()
    if not prompt:
        continue

    try:
        full_prompt = f"""### Instruction:
{prompt}

### Response:
"""

        result = model(full_prompt, max_tokens=128, stop=["###"])
        text = result["choices"][0]["text"].strip()
        print(text.replace("\n", " "), flush=True)

    except Exception as e:
        print("ERROR", flush=True)
